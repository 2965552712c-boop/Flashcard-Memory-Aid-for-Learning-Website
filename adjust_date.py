#!/usr/bin/env python3
from datetime import datetime
import sys

if len(sys.argv) > 1:
    date_str = sys.argv[1]
else:
    date_str = sys.stdin.read().strip()

try:
    dt = datetime.strptime(date_str, '%a %b %d %H:%M:%S %Y %z')
    new_dt = dt.replace(year=dt.year - 1)
    print(new_dt.strftime('%a %b %d %H:%M:%S %Y %z'))
except Exception as e:
    print(date_str)
