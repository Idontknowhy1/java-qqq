
mvnProfile=prod_01_jar
localJarFile=jike_admin_api_prod_01.jar
remoteIp=47.110.82.217
remoteJarHome=/root/data/apiadmin/jar
remoteDockerCompose=/root/data/apiadmin

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