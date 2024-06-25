const express = require('express');
const { createServer } = require('node:http');
const { join } = require('node:path');
const { Server } = require('socket.io');


const redis = require('redis');
const client = redis.createClient({
  host: '127.0.0.1', // Redis服务器的主机名或IP地址
  port: 6379, // Redis服务器的端口号
});

client.connect()

client.on('connect', () => {
  console.log('已连接到Redis服务器');
});

client.on('error', (err) => {
  console.error('Redis错误：', err);
});


const cors = require('cors');
const app = express();
const server = createServer(app);
const io = new Server(server,{ cors: true });


app.get('/', (req, res) => {
  res.sendFile(join(__dirname, 'index.html'));
});

app.get('/history/:id',(req,res)=>{
  let history = []
  client.lRange("history_"+req.params.id,-20,-1).then(val=>{
    res.send(val)
  })
})

io.on('connection', (socket) => {
  socket.on('chat message', (msg) => {
    console.log('收到消息： ' + msg.room);
    client.rPush("history_"+msg.room,JSON.stringify(msg))
    io.emit('chat message', msg);
  });
  });


server.listen(3000, () => {
  console.log('server running at http://localhost:3000');
});