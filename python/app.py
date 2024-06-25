from flask import Flask
import pandas as pd
import pymysql as pm
from sqlalchemy import create_engine
import random
from concurrent.futures import ThreadPoolExecutor
from scipy.stats import pearsonr
import numpy as np
import json
import redis

def anime_Jaccard(df,anime_x,anime_y):
    anime1_users = set(df[df['anime_id'] == anime_x]['user_id'])
    anime2_users = set(df[df['anime_id'] == anime_y]['user_id'])
    intersection = len(anime1_users.intersection(anime2_users))
    union = len(anime1_users.union(anime2_users))
    return intersection / union

def anime_rating_deficiency(df,anime_x,anime_y):
    book1_ratings = df[df['anime_id'] == anime_x]
    book2_ratings = df[df['anime_id'] == anime_y]
    merged_ratings = pd.merge(book1_ratings, book2_ratings, on='user_id')
    return merged_ratings.mean()["rating_x"]-merged_ratings.mean()["rating_y"]

def getSql(id):
    # 创建连接
    conn = pm.connect(host='localhost', user='root', password='bz1746578697', database='anime_server_side', charset='utf8')
    # 创建游标
    cursor = conn.cursor()
    # 执行SQL语句
    sql = f"select user_id,anime_id,rating from ratings where user_id = {id} and rating >=6"
    cursor.execute(sql)
    # 获取查询结果
    results = cursor.fetchall()
    # 关闭游标和连接
    cursor.close()
    conn.close()
    list = ['user_id','anime_id','rating']
    return pd.DataFrame(results,columns=list)

def rcm_result_protol(some_anime_id, some_result, df):
    def calculate_similarity(anime_id, result):
        return {
            "rcm_anime_id": result,
            "protol_anime_id": anime_id,
            "weight": anime_Jaccard(df, anime_id, result),
        }

    with ThreadPoolExecutor() as executor:
        similarity_list = list(executor.map(calculate_similarity, some_anime_id, some_result))

    unique_dict = {item["rcm_anime_id"]: item for item in sorted(similarity_list, key=lambda x: x['weight'], reverse=True)}

    return list(unique_dict.values())

def rcm_result_with_slopeone(some_protol_result,user_id,df):
    list = []
    for i in range(len(some_protol_result)):
        list.append({
            "rcm_anime_id":some_protol_result[i]["rcm_anime_id"],
            "protol_anime_id":some_protol_result[i]["protol_anime_id"],
            "predict_rating":df.loc[(df['user_id'] == user_id) & (df['anime_id'] == some_protol_result[i]["protol_anime_id"])]['rating'].tolist()[0]+anime_rating_deficiency(df,some_protol_result[i]['protol_anime_id'],some_protol_result[i]['rcm_anime_id'])
        })
    return sorted(list,key=lambda x: x['predict_rating'], reverse=True) 
    
def find_similiar_user(vector, df):
    print(4)
    def calculate_pearsonr(row):
        vector_or = row.fillna(0)
        pc = pearsonr(vector, vector_or)
        return pd.Series({"user_id": row.name, "statistic": pc[0], "pvalue": pc[1]})

    # 使用 ThreadPoolExecutor 并行计算皮尔逊相关系数
    with ThreadPoolExecutor() as executor:
        result = list(executor.map(calculate_pearsonr, [row for _, row in df.iterrows()]))

    result = pd.DataFrame(result)
    sorted_result = result.sort_values(by="statistic", ascending=False)

    top_users = sorted_result.head(100)["user_id"].tolist()

    # 使用向量化操作替换循环
    matrix = df.loc[top_users].fillna(0).values

    return pd.DataFrame(matrix).mean().sort_values(ascending=False).index

def remove_dumplicate_element(alist,blist):
    result = [x for x in alist if x not in blist]
    return result




# 主程序

app = Flask(__name__)

redis_client = redis.Redis(host='localhost', port=6379, db=0)
anime = pd.read_csv(r"python\anime_data\anime.csv")
rating = pd.read_csv(r"python\anime_data\rating.csv")
rating = rating.drop_duplicates(subset=['user_id', 'anime_id'])
rating['rating'] = rating['rating'].clip(lower=0)
user_rating_matrix = rating.pivot(index="user_id",columns="anime_id",values="rating")

@app.route('/')
def hello():
    return 'Hello, World!'

@app.route('/rcm/<id>')
def rcm(id):
    vector = pd.DataFrame(np.zeros((1,11200)),columns=user_rating_matrix.columns)
    sqls = getSql(id)
    for i in range(sqls.shape[0]):
        vector[sqls.loc[i]['anime_id']] = sqls.loc[i]['rating']
    rated_anime_ids = sqls['anime_id'].tolist()
    result = [item["rcm_anime_id"] for item in rcm_result_with_slopeone(rcm_result_protol(rated_anime_ids,find_similiar_user(vector.loc[0],user_rating_matrix),rating),int(id),sqls)]
    if(len(redis_client.lrange(f"result_{id}",0,-1))>0):
        redis_client.lrem(f"result_{id}",0,-1) 
    for item in remove_dumplicate_element(result,rated_anime_ids):
        redis_client.rpush(f"result_{id}",item)
    redis_client.expire(f"result_{id}",30*60)
    print("@Recall:")
    recall = len(set(result)&set(rated_anime_ids))/len(set(rated_anime_ids))
    print(recall)
    print("@Precision:")
    precision = len(set(result)&set(rated_anime_ids))/len(set(result))
    print(precision)
    print("F-measure:")
    f_measure = 2*recall*precision/(recall+precision)
    print(f_measure)
    return json.dumps(remove_dumplicate_element(result,rated_anime_ids))


if __name__ == '__main__':
    app.run(threaded=True,host='0.0.0.0')
