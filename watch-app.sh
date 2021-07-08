#!/bin/bash
appname=$1
appguid=$(cf app $appname --guid)

while true
do 
	output=$(cf curl /v2/apps/$appguid/instances | jq -r 'to_entries[] | [.key, .value.state] | @tsv')
	clear
	echo "$output"
done
