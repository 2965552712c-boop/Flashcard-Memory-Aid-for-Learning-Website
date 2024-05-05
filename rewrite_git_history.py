#!/usr/bin/env python3
import subprocess
import re
from datetime import datetime, timedelta

def get_commit_list():
    """获取所有 commit 的哈希值"""
    result = subprocess.run(['git', 'rev-list', '--all', '--reverse'], 
                          capture_output=True, text=True)
    return result.stdout.strip().split('\n')

def get_commit_info(commit_hash):
    """获取 commit 的详细信息"""
    result = subprocess.run(['git', 'show', '-s', '--format=%an%n%ae%n%ai%n%cn%n%ce%n%ci%n%s%n%b', commit_hash],
                          capture_output=True, text=True)
    lines = result.stdout.split('\n')
    return {
        'author_name': lines[0],
        'author_email': lines[1],
        'author_date': lines[2],
        'committer_name': lines[3],
        'committer_email': lines[4],
        'committer_date': lines[5],
        'subject': lines[6],
        'body': '\n'.join(lines[7:])
    }

def adjust_date(date_str):
    """将日期提前1年"""
    dt = datetime.fromisoformat(date_str.replace(' ', 'T', 1).rsplit(' ', 1)[0])
    tz = date_str.rsplit(' ', 1)[1]
    new_dt = dt.replace(year=dt.year - 1)
    return f"{new_dt.strftime('%Y-%m-%d %H:%M:%S')} {tz}"

# 执行 filter-branch
print("正在重写 Git 历史，将所有 commit 时间提前1年...")
print("这可能需要一些时间，请耐心等待...")

subprocess.run([
    'git', 'filter-branch', '-f', '--env-filter',
    '''
    import sys
    from datetime import datetime
    
    def adjust_date(date_str):
        try:
            parts = date_str.strip().split()
            # 解析年份并减1
            year_idx = 4
            year = int(parts[year_idx])
            parts[year_idx] = str(year - 1)
            return ' '.join(parts)
        except:
            return date_str
    
    import os
    author_date = os.environ.get('GIT_AUTHOR_DATE', '')
    committer_date = os.environ.get('GIT_COMMITTER_DATE', '')
    
    if author_date:
        os.environ['GIT_AUTHOR_DATE'] = adjust_date(author_date)
    if committer_date:
        os.environ['GIT_COMMITTER_DATE'] = adjust_date(committer_date)
    ''',
    '--', '--all'
], env={'FILTER_BRANCH_SQUELCH_WARNING': '1'})

print("\n完成！")
