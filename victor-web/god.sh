#!/bin/bash
echo "start success"

echo "check process"
pid=`adb shell ps | grep app_process | awk -F ' ' '{print $2}'`
if [[ -n "$pid" ]]; then
	adb shell kill $pid
	echo "kill process: $pid"
fi

point=`adb shell dumpsys window | grep mSystem= | awk -F '-' '{print $2}' | awk -F '(' '{print $2}' | awk -F ')' '{print $1}'`
width=`echo $point | awk -F ',' '{print $1}'`
height=`echo $point | awk -F ',' '{print $2}'`
echo "screen width: $width"
echo "screen height: $height"

package=`adb shell dumpsys activity top | grep ACTIVITY | awk '{print $2}' | awk -F '/' '{print $1}'`
echo "connect: $package"

adb forward tcp:56789 tcp:56789
echo "open port 56789"

cmd="export CLASSPATH=/data/app/$package-1/base.apk;exec app_process /system/bin com.shanbay.beaver.server.Main $width $height"
echo $cmd
adb shell $cmd
