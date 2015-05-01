#!/usr/bin/python

import os, sys, glob, zipfile, smtplib, getpass, MimeWriter, mimetools, base64
from pybars import Compiler

class ProjMailException(Exception):
    pass

def count_lines(filename):
    ext = filename.split(".")[-1]
    f = open(filename,"r").read().replace(" ","").replace("\t","").splitlines()
    lines = 0
    for line in f:
        if len(line) > 0:
            if ext == "c" or ext == "scala":
                if line[:2] != "/*" and line[0] != "*" and line[:2] != "*/" and line[:2] != "//":
                    lines += 1
            elif ext == "lisp" and line[0] != ";":
                lines += 1
            elif (ext == "prolog" or ext == "pl"):
                if line[:2] != "/*" and line[0] != "*" and line[:2] != "*/" and line[0] != "%":
                    lines += 1
        # to debug app:
        # print ext, lines, len(line), line
    return lines

def generate_page(data):

    compiler = Compiler()
    source = u"{{>header}}{{#list programs}}{{fileName}}{{lineCount}}{{/list}}"
    template = compiler.compile(source)

    def _list(this, options, items):
        result = [u"<ul>"]
        for thing in items:
            result.append(u"<li>")
            result.append(u"Source File:<a href=\"" + thing["fileName"] + "\">" + thing["fileName"] + "</a>")
            result.append(u" - " + str(thing["lineCount"]) + " lines without comments")
            result.append(u"</li>")
        result.append(u"</ul>")
        return result
    helpers = {"list": _list}

    # Add partials
    header = compiler.compile(u"<h1>Programs</h1>")
    partials = {"header": header}

    output = template(data,helpers=helpers,partials=partials)

    open("index.html","wb").write(output)

def zipproj(path, name):
    zipf = zipfile.ZipFile(name,"w")
    for root, dirs, files in os.walk(path):
        for file in files:
            if file != name:
                zipf.write(os.path.join(root, file))
    zipf.close()

def mail_archive(fromaddr,toaddr,archive):
    password = getpass.getpass("Enter gmail password: ")
    server = smtplib.SMTP('smtp.gmail.com:587')

    message = StringIO.StringIO()
    email_msg = MimeWriter.MimeWriter(message)
    email_msg.addheader('To', toaddr)
    email_msg.addheader('From', fromaddr)
    email_msg.addheader('Subject', "")
    email_msg.addheader('MIME-Version', '1.0')

    server.starttls()
    server.login(fromaddr,password)
    server.sendmail(fromaddr,toaddr,msg)
    server.quit()

def main(argv):

    valid_filenames = ["*.c","*.lisp","*.scala","*.prolog","*.pl","*.py"]
    data = {"programs":[]}

    if len(argv) < 2:
        raise ProjMailException("Not enough arguments given (try `projmail.py help`)")

    if argv[1] == "help":
        print "projmail.py <zip file name> <from gmail> <to email>"
        sys.exit(2)

    for d in xrange(6):
        filedir = "a" + str(d + 1)
        if os.path.isdir(filedir):
            for ext in valid_filenames:
                for f in glob.glob(filedir + "/" + ext):
                    data["programs"].append({"fileName":f,"lineCount":count_lines(f)})

    generate_page(data)
    zipproj(".",argv[1])

if __name__ == "__main__":
    main(sys.argv)
