import os

for i in os.listdir('.'):
    if i[-3:] == 'svg':
        with open(i, 'r') as f:
            org = f.read()
        os.remove(i)
        with open('_'+i, 'w') as f:
            f.write(org.replace('fill="currentColor"', 'fill="@color/currentColor"'))
        
        # os.rename(i, '_'+i)