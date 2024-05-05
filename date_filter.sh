#!/bin/bash

# 从日期字符串中提取年份并减1
adjust_date() {
    local date_str="$1"
    # 提取年份 (格式: Day Mon DD HH:MM:SS YYYY +ZZZZ)
    local year=$(echo "$date_str" | awk '{print $5}')
    local new_year=$((year - 1))
    # 替换年份
    echo "$date_str" | sed "s/ $year / $new_year /"
}

export GIT_AUTHOR_DATE=$(adjust_date "$GIT_AUTHOR_DATE")
export GIT_COMMITTER_DATE=$(adjust_date "$GIT_COMMITTER_DATE")
