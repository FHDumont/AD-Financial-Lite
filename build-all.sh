#!/bin/bash
#f=$( $0)
if [ `whoami` != root ]; then
    echo Please run this script as root or using sudo
    exit
fi

CWD=$(dirname $0)
# TOP=$(readlink -f ${CWD})
TOP=$(pwd)

SUB_PROJECTS=("agent-repo" "haproxy" "java-services" "load" "mongodb" "nodejs-services")

cd ${TOP}

for p in "${SUB_PROJECTS[@]}"; do
		
	echo "-------------- [ " ${p} " ] ------------------"

	if [ -d ${TOP}/${p} ]; then	
		echo "Found project, ${p}.  Changing directory to ${TOP}/${p}..."
		cd ${TOP}/${p}
	
		# if [ ${p} == 'agent-repo' ]; then
		# 	echo "Downloading Latest Machine Agent..."
		# 	./getAgent.sh download machine -v 21.6.0
		# 	echo "Extracting Machine Agent..."
		# 	mkdir MachineAgent
		# 	unzip -qq ./MachineAgent.zip -d MachineAgent
		# 	rm MachineAgent.zip

		# 	echo "Downloading Latest Java Agent..."
		# 	./getAgent.sh download java8 -v 21.6.0
		# 	echo "Extracting Java Agent..."
		# 	mkdir AppServerAgent
		# 	unzip -qq ./AppServerAgent.zip -d AppServerAgent
		# 	rm AppServerAgent.zip
		# fi

	 	if [ -f build.sh ]; then
			d=`date`
			echo "Building ${p} from ${TOP}/${p}"
			./build.sh
		else
			echo "Failed to build ${p}, ${TOP}/${p}/build.sh doesn't exist"
		fi
	else 
		echo "Failed to build ${p}, directory ${TOP}/${p} doesn't exist"
	fi

	echo "Changing to parent folder ${TOP}"	
	cd ${TOP}

	echo "--------------------------------------------------"
done

exit 1

