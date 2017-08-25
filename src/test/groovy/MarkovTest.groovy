import com.stewsters.utils.MarkovModel


MarkovModel markovModel = new MarkovModel()

markovModel.addCorpus("data/greekGods.txt")

int min = 5
int max = 12

100.times {
    println markovModel.generateSample(min, max)
}