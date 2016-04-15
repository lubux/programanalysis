# ETH Program Analysis Project 2016
Deep Learning for Programs
The goal of this project is to built a system that predicts JavaScript APIs. That is, given a partial
code snippet and a completion position, the system predicts ranked list of top n predictions (with their
probabilities) 
The project consists of two main parts – using static program analysis to extract sequences suitable
for training probabilistic models and training probabilistic model over the extracted sequences.
Program Analysis The first part consists of extracting sequences of API call invocations on the same
object that are used for training. This is similar to the technique presented in [4]. For example, for object
document in the code snippet above we can extract following sequence that captures how the object is used
– {hclearSelectors, 1i hquerySelectorAll, 0i hquerySelectorAll, 0i}. Here the sequence encodes the fact
that the document object was previously used twice as a target (denoted by position 0) when invoking the
querySelectorAll method and once as first argument in clearSelectors method. Note that the extracted
sequence does not need to be sound given the over-approximation of call graph and points-to analysis.
Probabilistic Models Given the extracted sequences one can directly train various probabilistic models
that will be used to predict the completion for new, unseen code snippets. Here, the n-gram model will
be used as a baseline and should be compared against neural language models[1] that have been recently
shown to improve the accuracy of language models also in the domain of modelling source code[4, 2].

# References
[1] Kombrink, S., Mikolov, T., Karafiat, M., and Burget, L. ´ Recurrent neural network based
language modeling in meeting recognition. In INTERSPEECH (2011), pp. 2877–2880.
[2] Maddison, C. J., and Tarlow, D. Structured generative models of natural source code. In
Proceedings of the 31th International Conference on Machine Learning, ICML 2014, Beijing, China,
21-26 June 2014 (2014), pp. 649–657.
[3] Raychev, V., Bielik, P., Vechev, M., and Krause, A. Learning programs from noisy data. In
Proceedings of the 43rd Annual ACM SIGPLAN-SIGACT Symposium on Principles of Programming
Languages (New York, NY, USA, 2016), POPL 2016, ACM, pp. 761–774.
[4] Raychev, V., Vechev, M., and Yahav, E. Code completion with statistical language models.
In Proceedings of the 35th ACM SIGPLAN Conference on Programming Language Design and
Implementation (New York, NY, USA, 2014), PLDI ’14, ACM, pp. 419–428.
