import re

def read_lines_from_file(file_path):
    lines = []
    with open(file_path, "r") as file:
        for line in file:
            lines.append(line.strip())
    return lines

def replace_transitions(transitions):
    for i in range(len(transitions)):
        if not transitions[i].strip():
            continue
        transitions[i] = re.search(r"(.*?)(T\d{2}|T\d{1})", transitions[i]).group(2)
    return "".join(transitions)

def apply_transition_replacements(transitions_line):
    return transitions_line.replace("T10", "TA").replace("T11", "TB").replace("T12", "TC")

def revert_transition_replacements(transitions_line):
    return transitions_line.replace("TA", "T10").replace("TB", "T11").replace("TC", "T12")

def apply_main_regex(transitions_line):
    regex = '(T1)(.*?)((T2)(.*?)(T4)(.*?)(T6)|(T3)(.*?)(T5)(.*?)(T7))(.*?)(T8)|(T9)(.*?)(TA)(.*?)(TB)(.*?)(TC)'
    substitution = '\g<2>\g<5>\g<7>\g<10>\g<12>\g<14>\g<17>\g<19>\g<21>'

    while len(transitions_line) > 0:
        temp_line = transitions_line
        transitions_line, num_replacements = re.subn(regex, substitution, transitions_line)

        if transitions_line == temp_line:
            print("\nPetri Net Invariants Failed: {}\n".format(revert_transition_replacements(transitions_line)))
            return

    print("\n-- Petri Net Invariants satisfied ! --\n")

def main():
    print("\n* --------------------------------------------------------- *\n")
    
    log_file_path = "data/log/transitions.log"
    transitions_list = read_lines_from_file(log_file_path)

    transitions_line = replace_transitions(transitions_list)
    transitions_line = apply_transition_replacements(transitions_line)

    apply_main_regex(transitions_line)
    
    print("\n* --------------------------------------------------------- *\n")

if __name__ == "__main__":
    main()
