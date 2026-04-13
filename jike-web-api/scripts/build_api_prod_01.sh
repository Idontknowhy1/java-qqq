
mvnProfile=prod_01_jar
localJarFile=jike_api_prod_01.jar
remoteIp=47.110.82.217
remoteJarHome=/root/data/apiv2/jar
remoteDockerCompose=/root/data/apiv2

cd ..

# 打包
mvn clean package -P ${mvnProfile};

# 上传
scp ./target/${localJarFile} root@${remoteIp}:${remoteJarHome};

# 重启
ssh root@${remoteIp} << EOF
  cd ${remoteDockerCompose}
  docker compose restart
  exit
EOF

cd ./scripts