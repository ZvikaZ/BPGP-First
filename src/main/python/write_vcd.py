import sys
import csv
import ctypes
from vcd import VCDWriter
from vcd import gtkw


def sanitizer(str):
    return str.replace(' ', '_').replace('[', '_').replace(']', '_').replace('(', '_').\
        replace(')', '_').replace(';','_').replace(',', '_')


with open("dump.csv") as csvfile:
    reader = csv.DictReader(csvfile, quoting=csv.QUOTE_ALL)
    data = list(reader)

inits = []
changes = []
dump_dict = {}

for line in data:
    var_name = f"{line['btName']}_{line['name']}"
    if var_name not in dump_dict:
        hashedVar = "var_" + str(ctypes.c_ulong(hash(var_name)))
        dump_dict[var_name] = hashedVar
        inits.append({'hashedVar': hashedVar, 'btName': line['btName'], 'name': line['name'], 'var_name': var_name,
                      'type': line['type']})

    try:
        value = int(line['value'])
    except:
        value = line['value']
    changes.append({'hashedVar': dump_dict[var_name], 'btName': line['btName'], 'name': line['name'], 'timestamp': int(line['timestamp']), 'value': value})

groups = {}

f = open("dump.vcd", "w")
with VCDWriter(f, timescale='1 ns', date='today') as writer:
    vars = {}
    for line in inits:
        if line['type'] == 'number':
            type = 'integer'
        elif line['type'] == 'object':
            type = 'string'
        else:
            # TODO - 'event' might be useful
            print("UNEXPECTED")
            import sys

            sys.exit(1)

        group = sanitizer(line['btName'])
        name = sanitizer(line['name'])
        if group not in groups:
            groups[group] = []
        groups[group].append(group + '.' + name)
        vars[line['hashedVar']] = writer.register_var(group, name, type)

    for line in changes:
        writer.change(vars[line['hashedVar']], line['timestamp'], line['value'])

f.close()

f = open("dump.gtkw", "w")
saver = gtkw.GTKWSave(f)
saver.dumpfile("dump.vcd")
for group in groups:
    with saver.group(group):
        for signal in groups[group]:
            assert len(groups[group]) == len(set(groups[group]))
            saver.trace(signal)
            print(signal)

