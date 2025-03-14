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

    # Jaccard系数 计算两个动画的粉丝重合度，得出动画的可能相似度
    #
def anime_Jaccard(df,anime_x,anime_y):
    anime1_users = set(df[df['anime_id'] == anime_x]['user_id'])
    anime2_users = set(df[df['anime_id'] == anime_y]['user_id'])
    intersection = len(anime1_users.intersection(anime2_users))
    union = len(anime1_users.union(anime2_users))
    return intersection / union

    # 计算两个动画的评分差值，得出用户对于动画的评分偏好
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

    # 通过Jaccard系数计算用户评分过后的动画与其他相似用户的喜欢的动画的相似度 Jaccard系数越高 说明两个作品的粉丝重合度越高 越有可能符合口味
    # some_anime_id 用户评分过的动画id
    # some_result 相似用户喜欢的动画
    # df 用户评分矩阵
    # 返回值为一个列表 包含了相似度最高的动画id 按照相似度降序排列
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

    # 通过slopeone算法计算用户评分过的动画与其他相似用户的评分的差值
    # some_protol_result 通过rcm_result_protol函数得出的结果
def rcm_result_with_slopeone(some_protol_result,user_id,df):
    list = []
    for i in range(len(some_protol_result)):
        list.append({
            "rcm_anime_id":some_protol_result[i]["rcm_anime_id"],
            "protol_anime_id":some_protol_result[i]["protol_anime_id"],
            "predict_rating":df.loc[(df['user_id'] == user_id) & (df['anime_id'] == some_protol_result[i]["protol_anime_id"])]['rating'].tolist()[0]+anime_rating_deficiency(df,some_protol_result[i]['protol_anime_id'],some_protol_result[i]['rcm_anime_id'])
        })
    return sorted(list,key=lambda x: x['predict_rating'], reverse=True) 


    # 寻找相似用户
def find_similiar_user(vector, df):
    print(4)

    # 定义皮尔森向量计算函数
    def calculate_pearsonr(row):
        vector_or = row.fillna(0)
        pc = pearsonr(vector, vector_or)
        return pd.Series({"user_id": row.name, "statistic": pc[0], "pvalue": pc[1]})

    # 使用 ThreadPoolExecutor 并行计算皮尔逊相关系数
    with ThreadPoolExecutor() as executor:
        result = list(executor.map(calculate_pearsonr, [row for _, row in df.iterrows()]))

    # 得出结果 转化成dataFrame 并根据相关系数降序排列
    result = pd.DataFrame(result)
    sorted_result = result.sort_values(by="statistic", ascending=False)

    # 得出相关度最高的100个用户
    top_users = sorted_result.head(100)["user_id"].tolist()

    # 使用向量化操作替换循环 得到相关系数最高的100个用户的用户评分矩阵
    matrix = df.loc[top_users].fillna(0).values

    # 计算每一列动画的评分平均值 降序排列输出结果
    return pd.DataFrame(matrix).mean().sort_values(ascending=False).index

def remove_dumplicate_element(alist,blist):
    result = [x for x in alist if x not in blist]
    return result




# 主程序

app = Flask(__name__)

# 链接redis服务器
redis_client = redis.Redis(host='localhost', port=6379, db=0)

# 读入anime以及rating数据集
anime = pd.read_csv(r"python\anime_data\anime.csv")
rating = pd.read_csv(r"python\anime_data\rating.csv")

# 删除user_id与anime_id相同的rating记录，去掉脏数据
rating = rating.drop_duplicates(subset=['user_id', 'anime_id'])


rating['rating'] = rating['rating'].clip(lower=0)

# 使用rating表与anime表组成用户评分矩阵
user_rating_matrix = rating.pivot(index="user_id",columns="anime_id",values="rating")

@app.route('/')
def hello():
    return 'Hello, World!'

@app.route('/rcm/<id>')
def rcm(id):

    # 创建空向量
    vector = pd.DataFrame(np.zeros((1,11200)),columns=user_rating_matrix.columns)

    # 获取特定用户的评分记录，['user_id','anime_id','rating']
    sqls = getSql(id)

    # 将空向量通过评分记录填充，获得用户评分向量
    for i in range(sqls.shape[0]):
        vector[sqls.loc[i]['anime_id']] = sqls.loc[i]['rating']

    # 获取用户评分过的动画id
    rated_anime_ids = sqls['anime_id'].tolist()


    result = [item["rcm_anime_id"] for item in rcm_result_with_slopeone(rcm_result_protol(rated_anime_ids,find_similiar_user(vector.loc[0],user_rating_matrix),rating),int(id),sqls)]

    # 删除redis中的缓存
    if(len(redis_client.lrange(f"result_{id}",0,-1))>0):
        redis_client.lrem(f"result_{id}",0,-1) 

    # 将结果存入redis
    for item in remove_dumplicate_element(result,rated_anime_ids):
        redis_client.rpush(f"result_{id}",item)
    
    # 设置过期时间
    redis_client.expire(f"result_{id}",30*60)

    # 输出Recall Precision F-measure 
    # Recall = (推荐列表与用户评分过的动画id的交集)/(用户评分过的动画id的并集)
    # Precision = (推荐列表与用户评分过的动画id的交集)/(推荐列表)
    # F-measure = 2*Recall*Precision/(Recall+Precision)

    print("@Recall:")
    recall = len(set(result)&set(rated_anime_ids))/len(set(rated_anime_ids))
    print(recall)
    print("@Precision:")
    precision = len(set(result)&set(rated_anime_ids))/len(set(result))
    print(precision)
    print("F-measure:")
    f_measure = 2*recall*precision/(recall+precision)
    print(f_measure)

    # 返回推荐列表
    return json.dumps(remove_dumplicate_element(result,rated_anime_ids))


if __name__ == '__main__':
    app.run(threaded=True,host='0.0.0.0',port=5000)
