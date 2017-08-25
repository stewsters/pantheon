package com.stewsters.pantheon

import com.stewsters.util.types.Gender
import com.stewsters.utils.MarkovModel

import static com.stewsters.util.math.MatUtils.d
import static com.stewsters.util.math.MatUtils.getBoolean
import static com.stewsters.util.math.MatUtils.getIntInRange
import static com.stewsters.util.math.MatUtils.rand
import static com.stewsters.util.math.MatUtils.randVal

public class GodGen {

    static def domains = [
            "Air"        : ["Clouds", "Lightning", "Wind", "Storms"],
            "Animal"     : ["Feathers", "Fur", "Insects"],
            "Artifice"   : ["Alchemy", "Construction", "Industry", "Toil", "Traps"],
            "Chaos"      : ["Demons", "Entropy", "Riots"],
            "Charm"      : ["Captivation", "Love", "Lust"],
            "Community"  : ["Cooperation", "Education", 'Family', 'the Home', "Dancing"],
            "Darkness"   : ["Loss", "the Moon", "Night"],
            "Death"      : ['Murder', 'Plagues', 'Undead'],
            "Destruction": ['Ruin'],
            "Disease"    : ['Plagues'],
            "Earth"      : ['Caves', 'Metal', 'Petrification', 'Radiation'],
            "Evil"       : ['Cannibalism', 'Corruption', 'Demons', 'Devils', 'Fear'],
            "Fire"       : ['Arson', 'Ash', 'Embers', 'Smoke'],
            'Glory'      : ['Heroism', 'Bravery', 'Hubris', 'Legends'],
            "Good"       : ['Friendship', 'Redemption'],
            "Healing"    : ['Medicine', 'Restoration', 'Resurrection'],
            "Knowledge"  : ['Education', 'Espionage', 'Memory', 'Thought'],
            "Law"        : ['Judgment', 'Legislation', 'Loyalty', 'Slavery', 'Tyranny'],
            "Luck"       : ['Curse', 'Fate'],
            "Madness"    : ['Insanity', 'Nightmares', 'Truth'],
            "Magic"      : ['Alchemy', 'Arcane', 'Divine', 'Rites'],
            "Nobility"   : ['Aristocracy', 'Hubris', 'Leadership'],
            "Plant"      : ['Decay', 'Growth', 'Thorns'],
            "Protection" : ['Defense', 'Fortifications', 'Purity', 'Solitude'],
            "Strength"   : ['Competition', 'Ferocity', 'Resolve'],
            "Sun"        : ['Day', 'Light', 'Thirst'],
            "Vermin"     : ['Decay', 'Pestilence'],
            "War"        : ['Blood', 'Duels', 'Tactics'],
            "Water"      : ['Ice', 'Oceans', 'Depths', 'Waves']
    ]

    static def favoredWeapons = [
            "battleaxe",
            "chakram",
            "dagger",
            "falchion",
            "flail",
            "greataxe",
            "knife",
            "longbow",
            "longsword",
            "mace",
            "maul",
            "morning star",
            "pick",
            "scimitar",
            "scythe",
            "short sword",
            "sickle",
            "spear",
            "staff",
            "warhammer",
            "whip",
    ]

    static Map<String, Map> creationTypes = [
            "ex nihilo"       : [
                    beginning: { String creator -> "nothing" },
                    then     : { String creator -> "" }
            ],
            "earth diver"     : [
                    beginning: { String creator -> "an endless ocean of primal water" },
                    then     : { String creator -> "" }
            ],
            "emergence"       : [
                    beginning: { String creator -> "dying old world" },
                    then     : { String creator -> "" }
            ],
            "dismemberment"   : [
                    beginning: { String creator -> "a great god named ${creator}" },
                    then     : { String creator -> "" }
            ],
            "cosmic egg"      : [
                    beginning: { String creator -> rand(["a cosmic egg", ""]) },
                    then     : { String creator -> "" }
            ],
            "primordial chaos": [
                    beginning: { String creator -> "nothing but formless chaos" },
                    then     : { String creator -> "${creator} separated " }
            ]
    ]

    static MarkovModel<Character> markovMaleModel // But why maleModels?
    static MarkovModel<Character> markovFemaleModel // But why femaleModels?
    static MarkovModel<Character> markovNeuterModel // But why models?

    static final Character start = '+' as Character
    static final Character end = '-' as Character

    public static void main(String[] args) {

        markovMaleModel = new MarkovModel<Character>(start, end)
        markovFemaleModel = new MarkovModel<Character>(start, end)
        markovNeuterModel = new MarkovModel<Character>(start, end)

        addCorpus(markovMaleModel, "data/greekGods.txt")
        addCorpus(markovNeuterModel, "data/greekGods.txt")

        addCorpus(markovFemaleModel, "data/greekGoddesses.txt")
        addCorpus(markovNeuterModel, "data/greekGoddesses.txt")

        // In the Beginning
        String creationType = rand(new ArrayList(creationTypes.keySet()))
        Map<String, Closure> creation = creationTypes[creationType]

        def creator = markovNeuterModel.generateSample(2, 8).join().capitalize()
        def creationStory = "In the Beginning there was ${creation.beginning(creator)}.  Then ${creation.then(creator)}"


        println creationStory

        List<Map> gods = []

        //First gen gods
        10.times {

            def (String name, String title, Gender gender) = getGenderAndName()

            def god = [
                    name   : name,
                    title  : title,
                    gender : gender.name(),
                    domains: (1..getIntInRange(1, 3)).collect { rand(domains.keySet().toList()) }.unique(),
                    weapon : rand(favoredWeapons),
                    power  : d(6) + d(6) + d(6)
            ]

            gods.add god
        }

        // Generate Children
        List<Map> godChildren = []
        gods.each { Map parentGod ->


            List<Map> parents = [parentGod]
            if (getBoolean(0.75)) {
                parents += rand(gods.findAll { it.gender != parentGod.gender })
            } else if (getBoolean(0.50)) {
                parents += rand([
                        [name: femaleName() + " the Cow"],
                        [name: maleName() + " the Bull"],
                        [name: femaleName() + " the Naiad"],
                        [name: femaleName() + " the Nymph"],
                        [name: femaleName() + " the Tree"]

                ])
            }

            parents.unique(true)


            def (String name, String title, Gender gender) = getGenderAndName()

            List<String> validDomains = parents*.domains.flatten().collect { domains[it] }.flatten() - null

            if (validDomains.contains(null))
                println validDomains.sort()


            def god = [
                    name   : name,
                    title  : title,
                    gender : gender,
                    domains: (1..getIntInRange(1, 3)).collect { rand(validDomains) }.unique(),
                    weapon : rand(favoredWeapons),
                    power  : getIntInRange(parents*.power.min(), parents*.power.max()),
                    parents: parents*.name
            ]

            godChildren.add god
        }

        println "\nfirst generation gods"
        gods.sort { -it.power }.each {
            println "${it.name}, the ${it.title} of ${capJoin(it.domains)}."
            println "wpn: ${it.weapon} pwr:${it.power}\n"
        }

        println "\nsecond generation gods"
        godChildren.sort { -it.power }.each {

            println "${it.name}, the ${it.title} of ${capJoin(it.domains)}."
            println "${getSonOrDaughter(it.gender)} of ${it.parents.join(' and ')}"
            println "wpn: ${it.weapon} pwr:${it.power}\n"
        }

        // Primordial Monsters - Things that have always existed

        // Demi Gods, monsters - things created by gods fucking about

        // Saints, heroes - Can be aligned to gods or ideals

    }

    static String capJoin(List<String> strings) {

        if (strings.size() <= 1) {
            return strings.join()
        }
        return strings[0..strings.size() - 2].join(', ') + " and " + strings[strings.size() - 1]
    }

    static String getSonOrDaughter(Gender gender) {
        switch (gender) {
            case Gender.MALE:
                return 'Son'
                break;
            case Gender.FEMALE:
                return 'Daughter'
                break;
            default:
                return 'Child'
                break
        }
    }


    static def getGenderAndName() {
        Gender gender = randVal(Gender.values())
        String name
        String title

        switch (gender) {
            case Gender.MALE:
                name = maleName()
                title = rand(["God", "Lord"])
                break
            case Gender.FEMALE:
                name = femaleName()
                title = rand(["Goddess", "Lady"])
                break
            default:
                name = neuterName()
                title = rand(["Manifestation", "Avatar", "Diety", "Divinity", "Master"])
                break
        }

        [name, title, gender]
    }

    static String maleName() {
        markovMaleModel.generateSample(4, 12).join().capitalize()
    }

    static String femaleName() {
        markovFemaleModel.generateSample(4, 12).join().capitalize()
    }

    static String neuterName() {
        markovNeuterModel.generateSample(4, 12).join().capitalize()
    }

    public static void addCorpus(MarkovModel<Character> markovModel, String filename) {

        File f = new File(filename)
        f.eachLine { line ->
            ArrayList<Character> sampleList = line.toLowerCase().toCharArray().collect { new Character(it) }

            Character[] sampleArr = new Character[sampleList.size()]
            sampleArr = sampleList.toArray(sampleArr)

            markovModel.addSample(sampleArr);
        }

    }

}
