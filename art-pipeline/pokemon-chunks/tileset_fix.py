with open('chunk_3.tmx', 'r') as f:
    data = f.read()

for i in range(21001, 21001 + 21000):
    data = data.replace(str(i), str(i - 21000))

with open('chunk_3.tmx', 'w') as f:
    f.write(data)
