#!/bin/bash
#*********************************************************
#monitor download directory. 
#One moniter.sh process is started for one download task.
#IF some url in $downloadDir/link can't be reached, monitor
#will log "WARNING". If load failed, log "ERROR". If 
#finished, log "FINISH".
#mail to $mailAddress.
#
#Last edited 2013.09.04 by Lyuxd. 
#
#*********************************************************


#every $interval sec check the size of wgetlog.
interval=30

#if size of wgetlog stay the same, try $maxtrytimes to check
maxtrytimes=20


downloadDir=$1
command=$2

rootDir=${PWD}
dataDir=$rootDir"/data"
logDir=$rootDir"/log"
log=$logDir"/monitor.log"
wgetlogDir="$downloadDir/wgetlog"
wgetlogname="wget-log"
wgetlog="$wgetlogDir/$wgetlogname"
failedqueue="$logDir/failedQueue"
runningTask="$logDir/runningTask"
mailAddress="15822834587@139.com"
lastERROR="e"
addtoBoolean=0


cd $downloadDir
sleep 1
counterMail=0


echo "`date "+%Y.%m.%d-%H:%M:%S--"`Monitor for directory: ${PWD}.">> $log
whereAmI=$(echo ${PWD} | awk -F "/" '{print $NF}')
if [ ! -d $wgetlogDir ]; then
mkdir $wgetlogDir
fi
# Put current task into runningTask is case of power off. When checkmodifiedLinkFile.sh up, runningTask will be checked if some task  interrupted. And interrupted task will be started again by checkmodifiedLinkFile.sh .  
isexit=$(grep $downloadDir $runningTask)
if [ -z "$isexit" ];then
echo $downloadDir >> $runningTask 
fi

#Begainning downloading.
`$command -b -o "$wgetlog" &`


#Check the size of logfile every $interval times.
#Continue cheching Until size is same with it in
#last check, then wait a $interval long period time,
#try again, try again...(try $maxtrytimes totally)
#read in wgetlog to find if there is
#something not right.
#Mail to $mailAddress.
trytimesRemain=$maxtrytimes
logoldsize=0
sleep 10
lognewsize=$(echo $(ls -l $wgetlog | awk '{print $5}'))
while [ ! -z "$lognewsize" ] && [ "$trytimesRemain" -gt 0 ]
do


# If log's size stays unchanging in $interval*$maxtrytime
# find "FINISH" from log. 
# 
	if [ "$lognewsize" -eq "$logoldsize" ];then
		message=$(tail -n3 "$wgetlog")
		level=$(echo $message|grep -E "FINISH|fully")
		if [ -z "$level" ];then
			trytimesRemain=`expr $trytimesRemain - 1`
			echo "`date "+%Y.%m.%d-%H:%M:%S--"`WARNNING: $downloadDir Download speed 0.0 KB/s. MaxTryTimes=$maxtrytimes. Try(`expr $maxtrytimes - $trytimesRemain`). ">> $log
		else
			break
		fi
	else
		trytimesRemain=$maxtrytimes
	fi


	ERROR=$(tail -n250 "$wgetlog" | grep -E "ERROR|failed")
	if [ ! -z "$ERROR" ] && [ "$ERROR" != "$lastERROR" ] && [ "$counterMail" -lt 5 ]
		then
		echo "`date "+%Y.%m.%d-%H:%M:%S--"`WARNNING: $downloadDir $ERROR. mail to $mailAddress.">> $log
		echo -e "${PWD}\n$ERROR\n"|mutt -s "Wget Running State : WARNNING in $whereAmI" $mailAddress
		counterMail=$counterMail+1
		lastERROR=$ERROR
		addtoBoolean=1
	fi
	logoldsize=$lognewsize
	sleep $interval
	lognewsize=$(echo $(ls -l $wgetlog | awk '{print $5}'))
done

if [ ! -z "$level" ]
	then
	echo "`date "+%Y.%m.%d-%H:%M:%S--"`FINISHI: $message. mail to $mailAddress.">> $log
	echo -e "`date '+%Y-%m-%d +%H:%M:%S'`\n${PWD}\n$message\n"|mutt -s "Wget Report : FINISH $whereAmI--RUNNING $(ps -A|grep -c wget)" $mailAddress
	counterMail=$counterMail+1
else
	echo "`date "+%Y.%m.%d-%H:%M:%S--"`ERROR: $message. mail to $mailAddress.">> $log
	echo -e "`date '+%Y-%m-%d +%H:%M:%S'`\n${PWD}\n$message\n"|mutt -s "Wget Report : ERROR in $whereAmI" $mailAddress
	addtoBoolean=1
	counterMail=$counterMail+1
fi

if [ "$addtoBoolean" -eq "1" ];then
echo "$downloadDir" >> "$failedqueue"
fi


#Remove the interrupted task from runningTask.
sed -i "/$whereAmI/d" "$runningTask"
echo "`date "+%Y.%m.%d-%H:%M:%S--"`$downloadDir Monitor ending.">> $log
