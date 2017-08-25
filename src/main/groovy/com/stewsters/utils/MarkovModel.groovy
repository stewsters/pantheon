package com.stewsters.utils

import static com.stewsters.util.math.MatUtils.rand

public class MarkovModel<T> {

    private Map<T, ArrayList<T>> markovChain
    final T start
    final T end

    public MarkovModel(T start, T end) {
        this.start = start
        this.end = end

        markovChain = new Hashtable<T, ArrayList<T>>()
        markovChain.put(start, new ArrayList<T>())
//        markovChain.put(end, new ArrayList<T>())
    }

    public void addSample(T[] sample) {

        for (int i = 0; i < sample.length; i++) {

            if (i == 0) {
                ArrayList<T> startWords = markovChain.get(start);
                startWords.add(sample[0]);

            } else {
                ArrayList<T> suffix = markovChain.get(sample[i]);
                if (suffix == null) {
                    suffix = new ArrayList<T>();
                    markovChain.put(sample[i], suffix);
                }

                if (i == sample.length - 1) {
                    suffix.add(end)
                } else
                    suffix.add(sample[i + 1])
            }
        }

    }

    public ArrayList<T> generateSample(int min, int max) {

        ArrayList<T> newPhrase = new ArrayList<T>()
        T nextWord = rand(markovChain.get(start))

        // Keep looping through the words until we've reached the end
        while (!nextWord.equals(end)) {

            newPhrase.add(nextWord)
            ArrayList<T> nextOptions = markovChain.get(nextWord)

            if (newPhrase.size() < min) {
                nextOptions = nextOptions - end
            }

            if (newPhrase.size() >= max)
                break

            if (!nextOptions)
                break

            nextWord = rand(nextOptions)
            if (nextWord == end) {
                break
            }
        }

        return newPhrase
    }
}
