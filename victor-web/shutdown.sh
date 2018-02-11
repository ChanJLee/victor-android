pid=`adb shell ps | grep app_process | awk -F ' ' '{print $2}'`
if [[ -n "$pid"  ]]; then
    adb shell kill $pid
    echo "kill process: $pid"
fi
echo "end"
