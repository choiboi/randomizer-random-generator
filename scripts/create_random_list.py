import sys

def createList(size, fname):
    print "Creating a list of size " + str(size) + "...."
    
    # Open file.
    f = open(fname, "w")
    
    # Write size number of lines.
    for i in range(size):
        f.write("line-" + str(i) + "\n")
    
    f.close()
    print "Finished creating list."
    

if __name__ == "__main__":
    # Get the filename.
    print sys.argv
    if len(sys.argv) > 1:
        fname = sys.argv[1]
    
        # Set default list size.
        size = 100
        # Get the list size from the argument.
        if len(sys.argv) > 2:
            size = int(sys.argv[2])
    
        createList(size, fname)
    else:
        # Return message indicating argument issue.
        print "Missing arguments. Following format: "
        print "python create_random_list.py <filename> <[optional] list size>"
    