import json

config_file_stream = open('config.json')

config_data = json.load(config_file_stream)

config_title = config_data['title']
file_map = config_data['files']

readme = ''

# make the title and subtitle
readme += '<h1>{}</h1>\n'.format(config_title)
readme += '<p>Please check the individual markdown files or refer to the dropdowns below...</p>\n'

for file, title in file_map.items():
    # read the file
    with open(file, 'r') as f:
        readme += '''<details>
                <summary>{}</summary>
                    {}</details>\n'''.format(title, f.read())

with open('README.md','w') as f:
    f.write(readme)

# close streams
config_file_stream.close()