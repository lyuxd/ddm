#!/bin/bash
#*************************************************
#This script reads in modifiedLinkFile, 
#for every record calling monitor.sh.
#monitor.sh /home/class/name "wget -c -i link -b"
#
#last edited 2013.09.04 by lyuxd.
#
#*************************************************



interval=10
rootDir=${PWD}
dataDir=$rootDir"/data"
logDir=$rootDir"/log"
failedqueue="$logDir/failedQueue"
runningTask="$logDir/runningTask"
modifiedLinkFile="$logDir/modifiedlinkfile"
modifiedLinkFileMT="$logDir/modifiedLinkFile.MT"
log=$logDir"/check.log"
maxWgetProcess=5
echo "`date "+%Y.%m.%d-%H:%M:%S--INFO: "`check is running...">>$log
#*****************************************
#-----------restart interrupted tasks-----
#*****************************************
if [ -f "$runningTask" ]; then
   while read line
   do
	counterWgetProcess=$(ps -A|grep -c "monitor.sh")
	while [ $counterWgetProcess -ge $maxWgetProcess ]
	do
		sleep 20
		counterWgetProcess=$(ps -A|grep -c "monitor.sh")
	done
	echo "`date "+%Y.%m.%d-%H:%M:%S--INFO: "`Call ./monitor for runningtask $line." >> $log 
	nohup "./monitor.sh" "$line" "wget -nd -c -i link -b" >> /dev/null &
	sleep 1
   done <$runningTask 
fi 


#*********************************
#------------failedQueue-----
#*********************************
#if [ -f "$failedqueue" ] && [ `ls -l "$failedqueue"|awk '{print $5}'` -gt "0" ];then
#	line=($(awk '{print $0}' $failedqueue))
#	echo ${line[1]}
#	:>"$failedqueue"
#	for ((i=0;i<${#line[@]};i++))
#	do
#	counterWgetProcess=$(ps -A|grep -c "monitor.sh")
#		while [ $counterWgetProcess -ge $maxWgetProcess ]
#		do
#			sleep 20
#			counterWgetProcess=$(ps -A|grep -c "monitor.sh")
#		done
#		echo "./monitor.sh" "${line[i]}" "wget -nd -c -i link -b"
#		"./monitor.sh" "${line[i]}" "wget -nd -c -i link -b" >> /dev/null &
#ex "$failedqueue" <<EOF
#1d
#wq
#EOF
#	done
#fi
#***************************************************
#------------check new task in modifiedLinkFile-----
#***************************************************
if [ ! -f "$modifiedLinkFile" ];then
	echo "`date "+%Y.%m.%d-%H:%M:%S--"`No modifiedLinkFile found. checkmodifiedLinkFiles.sh exit 1." >> $log
	exit 1
fi
if [ ! -f "$modifiedLinkFileMT" ];then
	echo "0" > "$modifiedLinkFileMT"
fi
while true
do

newMT=$(stat -c %Y $modifiedLinkFile|awk '{print $0}')
oldMT=$(awk '{print $0}' "$modifiedLinkFileMT")

if [ "$newMT" != "$oldMT" ]; then
while read line
do	
	if [ ! -z "$line" ] && [ "$line" != "" ]; then
		counterWgetProcess=$(ps -A|grep -c "monitor.sh")
		while [ $counterWgetProcess -ge $maxWgetProcess ]
		do
			#echo "waiting 20sec"
			sleep 20
			counterWgetProcess=$(ps -A|grep -c "monitor.sh")
		done
			#newLink=$(echo $line |awk '{print $2}')
			newLink=$line
			linkfileName=$(echo $newLink |awk -F "/" '{print $NF}')
			downloadDir=$(echo $newLink|awk -F "$linkfileName" '{print $1}')
			echo "`date "+%Y.%m.%d-%H:%M:%S--INFO: "`Call ./monitor for $downloadDir." >> $log
			"./monitor.sh" "$downloadDir" "wget -nd -c -i $linkfileName -b" >> /dev/null &
			sleep 1
			sed -i "1d" "$modifiedLinkFile"
	fi
done <$modifiedLinkFile

echo $(stat -c %Y $modifiedLinkFile|awk '{print $0}') > "$modifiedLinkFileMT"
#else
	#echo "nothing to do"
fi
sleep $interval
done
