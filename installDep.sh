#Used Commands
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer
sudo apt-get install oracle-java8-set-default

sudo apt-get install python 

sudo apt-get install nodejs

sudo apt-get install python-pip python-dev build-essential
sudo pip install --upgrade pip 
sudo pip install --upgrade virtualenv 

sudo pip install numpy
sudo pip install --upgrade https://storage.googleapis.com/tensorflow/linux/cpu/tensorflow-0.8.0-cp27-none-linux_x86_64.whl

sudo apt-get install git

#SRILM by hand
sudo mkdir /usr/share/srilm
mv srilm-1.7.1.tar.gz /usr/share/srilm
cd /usr/share/srilm
sudo tar xzf srilm-1.7.1.tar.gz 
sudo nano Makefile
#Change srlim variable in makefile to /usr/share/srilm
sudo nano common/Makefile.machine.i686-m64
#Add -fPIC flags
#ADDITIONAL_CFLAGS = -fopenmp -fPIC
#ADDITIONAL_CXXFLAGS = -fopenmp -fPIC
sudo make
sudo cp /usr/share/srilm/bin/i686-m64/* /usr/local/bin


sudo pip install Cython
cd
git clone https://github.com/njsmith/pysrilm.git
cd pysrilm/
sudo nano setup.py
#Change SRILM_DIR to /usr/share/srilm in setup.py
sudo python setup.py install

cd
wget http://www.srl.inf.ethz.ch/pa2016/json_printer_v1.tar.gz
tar -xvzf json_printer_v1.tar.gz
rm -f json_printer_v1.tar.gz
cd json_printer
sudo apt-get install libgoogle-glog-dev libgflags-dev libjsoncpp-dev cmake g++
sudo ./build.sh

cd
git clone https://github.com/lubux/programanalysis.git
