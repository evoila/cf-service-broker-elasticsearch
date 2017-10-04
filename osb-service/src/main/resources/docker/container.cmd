export REPOSITORY_REDIS=$repo_service &&
export REPOSITORY_MAIN=$repo_main &&
apt-get update &&
apt-get install -y wget &&
wget $repo_service/redis-template.sh --no-cache &&
chmod +x redis-template.sh &&
./redis-template.sh -n $database_number -p $database_password -e docker
