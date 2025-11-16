# Hadoop-BigData
# Configuração - Bashrc:
Tive que colar isso no .bashrc:

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export HADOOP_HOME=/usr/local/hadoop
export HADOOP_INSTALL=$HADOOP_HOME
export HADOOP_MAPRED_HOME=$HADOOP_HOME
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_HDFS_HOME=$HADOOP_HOME
export YARN_HOME=$HADOOP_HOME
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
export PATH=$PATH:$HADOOP_HOME/sbin:$HADOOP_HOME/bin

# Configuração - hadoop-env.sh
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

# Configuração - hdfs-site.xml
<configuration>
  <property>
    <name>dfs.replication</name>
    <value>1</value>
  </property>

  <property>
    <name>dfs.namenode.name.dir</name>
    <value>file:/usr/local/hadoop_tmp/hdfs/namenode</value>
  </property>

  <property>
    <name>dfs.datanode.data.dir</name>
    <value>file:/usr/local/hadoop_tmp/hdfs/datanode</value>
  </property>
</configuration>

# Configuração - core-site.xml
<configuration>
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://localhost:9000</value>
  </property>
</configuration>

# Configuração - Antes de Executar:
sudo rm -r /usr/local/hadoop_tmp/hdfs/namemode

sudo mkdir -p /usr/local/hadoop_tmp/hdfs/namenode
sudo mkdir -p /usr/local/hadoop_tmp/hdfs/datanode
sudo chown -R $USER:$USER /usr/local/hadoop_tmp
hdfs namenode -format
start-dfs.sh
start-yarn.sh
jps
hdfs dfs -ls /
hdfs dfs -mkdir -p /user/raindrop/input
hdfs dfs -mkdir -p /user/raindrop/output
cd Desktop

# UPLOAD DO ARQUIVO NO HDFS:
hdfs dfs -put ./netflix_titles.csv /user/raindrop/input/

# Executar Processo:
hadoop jar ./target/Hadoop-BigData-1.0-SNAPSHOT.jar org.example.HadoopMain /user/raindrop/input/netflix_titles.csv /user/raindrop/output/

# Trazer arquivo do Hadoop pro Local:
mkdir -p ~/Desktop/DEV/Hadoop-BigData/output_local

hdfs dfs -get /user/raindrop/output/part-r-00000 ~/Desktop/DEV/Hadoop-BigData/output_local/