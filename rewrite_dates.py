#!/usr/bin/env python3
import subprocess
import sys
from datetime import datetime, timedelta

def adjust_date(date_str):
    """将日期字符串提前1年"""
    try:
        # 解析 Git 日期格式
        dt = datetime.strptime(date_str.strip(), "%a %b %d %H:%M:%S %Y %z")
        # 提前1年
        new_dt = dt.replace(year=dt.year - 1)
        # 返回 Git 格式
        return new_dt.strftime("%a %b %d %H:%M:%S %Y %z")
    except Exception as e:
        print(f"Error parsing date: {date_str}, {e}", file=sys.stderr)
        return date_str

# 从环境变量读取日期
import os
author_date = os.environ.get('GIT_AUTHOR_DATE', '')
committer_date = os.environ.get('GIT_COMMITTER_DATE', '')

if author_date:
    new_author_date = adjust_date(author_date)
    print(f'export GIT_AUTHOR_DATE="{new_author_date}"')

if committer_date:
    new_committer_date = adjust_date(committer_date)
    print(f'export GIT_COMMITTER_DATE="{new_committer_date}"')
