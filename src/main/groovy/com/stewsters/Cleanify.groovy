package com.stewsters
/**
 * Created by adrian.moore on 7/10/17.
 */

def file = new File("data/greekGoddessesRaw.txt")

List<String> names = []

file.eachLine {
    if (!it)
        return

    names += it.split()[0].replaceAll(':', '').toLowerCase()
}

def outFile = new File('data/greekGoddesses.txt')
outFile.write(names.unique().sort().join("\n"))



