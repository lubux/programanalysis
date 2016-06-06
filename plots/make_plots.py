import numpy as np
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages
import math

fig_width_pt = 300.0                        # Get this from LaTeX using \showthe
inches_per_pt = 1.0/72.27*2                 # Convert pt to inches
golden_mean = ((math.sqrt(5)-1.0)/2.0)*.8   # Aesthetic ratio
fig_width = fig_width_pt*inches_per_pt      # width in inches
fig_height =(fig_width*golden_mean)           # height in inches
fig_size = [fig_width,fig_height/1.5]

params = {'backend': 'ps',
    'axes.labelsize': 16,
    'text.fontsize': 16,
    'legend.fontsize': 14,
    'xtick.labelsize': 14,
    'ytick.labelsize': 14,
    'font.size': 14,
    'figure.figsize': fig_size,
    'font.family': 'times new roman'}


def bar_plot_pred(data, title, filename):
    x_titles = ("Top 1", "Top 2", "Top 3", "Top 4", "Top 5")

    (N, _) = data.shape

    pdf_pages = PdfPages(filename)
    plt.rcParams.update(params)
    plt.rc('pdf', fonttype=42)

    ind = np.arange(N)
    width = 0.30
    fig, ax = plt.subplots()

    ngram = plt.bar(ind, data[:, 0], width, color='0.1', label='3-gram')
    rnn = plt.bar(ind + width, data[:, 1], width, color='0.7', label='lstm-rnn', hatch="///")
    comb = plt.bar(ind + 2 * width, data[:, 2], width, color='0.3', label='combined', hatch='\\\\')

    ax.set_xlim(-width, len(ind)+width)
    ax.set_ylim(0, 100)
    ax.set_ylabel('Correct prediction in [%]')
    ax.set_title(title)
    ax.set_xticks(ind + (3*width / 2))
    ax.set_xticklabels(x_titles)

    def autolabel(rects):
        # attach some text labels
        for rect in rects:
            height = rect.get_height()
            ax.text(rect.get_x() + rect.get_width()/2., 1.02*height,
                    '%d%%' % int(height),
                    ha='center', va='bottom', fontsize=12)

    autolabel(ngram)
    autolabel(rnn)
    autolabel(comb)

    plt.legend(bbox_to_anchor=(0.002, 0.89, 0.996, .102), loc=0,
               ncol=3, mode="expand", borderaxespad=0.1)
    F = plt.gcf()
    F.set_size_inches(fig_size)
    pdf_pages.savefig(F, bbox_inches='tight')
    plt.clf()
    pdf_pages.close()


# data from web test set
data_web = [[46.67, 30.00, 46.67],
            [63.33, 50.00, 63.33],
            [66.67, 56.67, 66.67],
            [73.33, 63.33, 73.33],
            [73.33, 63.33, 73.33]]
data_web = np.asarray(data_web)

bar_plot_pred(data_web, 'Web Test-Set Scores', 'web_data_plot.pdf')

# data from nodejs test set
data_node = [[43.33, 26.67, 43.33],
             [56.67, 43.33, 56.67],
             [60.00, 46.67, 60.00],
             [60.00, 50.00, 60.00],
             [66.67, 50.00, 66.67]]
data_node = np.asarray(data_node)

bar_plot_pred(data_node, 'NodeJS Test-Set Scores', 'node_data_plot.pdf')

# data from own test set
data_tutorial = [[53.33, 30.00, 53.33],
                 [63.33, 36.67, 63.33],
                 [63.33, 40.00, 63.33],
                 [63.33, 40.00, 63.33],
                 [63.33, 43.33, 63.33]]
data_tutorial = np.asarray(data_tutorial)

bar_plot_pred(data_tutorial, 'Tutorial Test-Set Scores', 'tutorial_data_plot.pdf')
