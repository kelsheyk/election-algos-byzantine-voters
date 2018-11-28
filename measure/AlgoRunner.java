package measure;
/*
Create a VoterData, run it (should move that to be part of constructor)
for each algorithm we're testing:
  for each set of voter prefs in VoterData's VoterDataCollection (50 of them)
    send pref list to algorithm
    get algo's ranking back
    calculate distance from ideal pairs
    store distance for later averaging

    ... wait we need to know the candidate count and good probability here. I either need to move some of the run() code in VoterData over here
    ... or store the candidate and goob prob info with VoterDataCollection so we can read it here
    probably the former
    yeah my ReadMe suggests VoterData be given the candidate count and good prob. I'vebeen acting like it's going to do everything.
    Which is fine it's not worth moving stuff around for a school project, as long as that class is doing everything we need it to do.

 */
public class AlgoRunner {
}
