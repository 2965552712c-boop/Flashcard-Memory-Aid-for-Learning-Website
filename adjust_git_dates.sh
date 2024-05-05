#!/bin/bash

# 获取原始日期的时间戳
AUTHOR_TIMESTAMP=$(date -d "$GIT_AUTHOR_DATE" +%s 2>/dev/null || echo "")
COMMITTER_TIMESTAMP=$(date -d "$GIT_COMMITTER_DATE" +%s 2>/dev/null || echo "")

# 如果时间戳为空，尝试使用 Python
if [ -z "$AUTHOR_TIMESTAMP" ] || [ -z "$COMMITTER_TIMESTAMP" ]; then
    python3 -c "
import os
from datetime import datetime, timedelta

author_date = os.environ.get('GIT_AUTHOR_DATE', '')
committer_date = os.environ.get('GIT_COMMITTER_DATE', '')

if author_date:
    dt = datetime.strptime(author_date.strip(), '%a %b %d %H:%M:%S %Y %z')
    new_dt = dt.replace(year=dt.year - 1)
    print(f'export GIT_AUTHOR_DATE=\"{new_dt.strftime(\"%a %b %d %H:%M:%S %Y %z\")}\"')

if committer_date:
    dt = datetime.strptime(committer_date.strip(), '%a %b %d %H:%M:%S %Y %z')
    new_dt = dt.replace(year=dt.year - 1)
    print(f'export GIT_COMMITTER_DATE=\"{new_dt.strftime(\"%a %b %d %H:%M:%S %Y %z\")}\"')
"
else
    # 减去一年的秒数 (365.25 * 24 * 60 * 60)
    YEAR_SECONDS=31557600
    
    NEW_AUTHOR_TIMESTAMP=$((AUTHOR_TIMESTAMP - YEAR_SECONDS))
    NEW_COMMITTER_TIMESTAMP=$((COMMITTER_TIMESTAMP - YEAR_SECONDS))
    
    export GIT_AUTHOR_DATE="$(date -d @$NEW_AUTHOR_TIMESTAMP)"
    export GIT_COMMITTER_DATE="$(date -d @$NEW_COMMITTER_TIMESTAMP)"
fi
