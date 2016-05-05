

class FilePreprocessor:
    START_TOKEN = "<h-start>"
    END_TOKEN = "<h-end>"
    UNKNOWN_TOKEN = "<unk>"

    def __init__(self, data_file_path, file_count_path=None):
        if file_count_path is None:
            self.word_to_count = self._build_count_dict(data_file_path)
        else:
            self.word_to_count = self._load_SIRLM_count_file(file_count_path)
        self.data_file_path = data_file_path

    def _build_count_dict(self, file_data_path):
        word_to_count = {}
        with open(file_data_path, "r") as f:
            for line in f:
                split = line.split()
                for word in split:
                    if word in word_to_count:
                        count = word_to_count[word]
                        count += 1
                        word_to_count[word] = count
                    else:
                        word_to_count[word] = 1
        return word_to_count

    def _load_SIRLM_count_file(self, file_count_path):
        word_to_count = {}
        with open(file_count_path, "r") as f:
            for line in f:
                split = line.split()
                if len(split) != 2:
                    continue
                word_to_count[split[0]] = int(split[1])
        return word_to_count

    def get_final_vocab_size(self):
        return len(self.word_to_count) + 3

    def preprocess_file(self, out_path, max_vocab_size):
        words_allowed = max_vocab_size-3
        vocab = self.word_to_count.items()
        erase_words = set()
        if len(vocab)>(words_allowed):
            srt_vocab = sorted(vocab, key=lambda tup: tup[1], reverse=True)
            for tup in srt_vocab[words_allowed:]:
                erase_words.add(tup[0])

        with open(self.data_file_path, "r") as f, open(out_path, "w") as out:
            for line in f:
                split = line.split()
                for word in split:
                    if word in erase_words:
                        line = line.replace(word, self.UNKNOWN_TOKEN)
                line = line.replace("\n", self.END_TOKEN)
                out.write("%s%s%s" % (self.START_TOKEN, line, " "))





