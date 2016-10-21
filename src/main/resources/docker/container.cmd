export REPOSITORY_ELASTICSEARCH=$repo_service &&
export REPOSITORY_MONIT=$repo_monit &&
export REPOSITORY_MAIN=$repo_main &&
apt-get update &&
apt-get install -y wget &&
wget $repo_service/elasticsearch-template.sh --no-cache &&
chmod +x elasticsearch-template.sh &&
./elasticsearch-template.sh -p $es_password -e docker
