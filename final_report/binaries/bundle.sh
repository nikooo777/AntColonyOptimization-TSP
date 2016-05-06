#!/bin/bash
echo "the output will be written to solutions.txt"
echo "$(date) - eil76" 
nice -n -20 taskset 0x1 java -jar algo.cup.niko-0.0.1-SNAPSHOT.jar eil76 0.1 2 0.1 0.985 0 15 179500 42 | grep 'Seed for' >> solutions.txt
echo "$(date) - kroA100" 
nice -n -20 taskset 0x1 java -jar algo.cup.niko-0.0.1-SNAPSHOT.jar kroA100 0.1 2 0.1 0.985 0 15 179500 1234567890 | grep 'Seed for' >> solutions.txt
echo "$(date) - ch130" 
nice -n -20 taskset 0x1 java -jar algo.cup.niko-0.0.1-SNAPSHOT.jar ch130 0.1 2 0.1 0.985 0 15 179500 69 | grep 'Seed for' >> solutions.txt
echo "$(date) - d198" 
nice -n -20 taskset 0x1 java -jar algo.cup.niko-0.0.1-SNAPSHOT.jar d198 0.1 1 0.1 0.95 0.9 18 179500 1337 | grep 'Seed for' >> solutions.txt
echo "$(date) - lin318" 
nice -n -20 taskset 0x1 java -jar algo.cup.niko-0.0.1-SNAPSHOT.jar lin318 0.1 2 0.1 0.955 0 15 179500 16071993 | grep 'Seed for' >> solutions.txt
echo "$(date) - pr439" 
nice -n -20 taskset 0x1 java -jar algo.cup.niko-0.0.1-SNAPSHOT.jar pr439 0.1 2 0.1 0.95 0.5 25 179500 8008135 | grep 'Seed for' >> solutions.txt
echo "$(date) - pcb442" 
nice -n -20 taskset 0x1 java -jar algo.cup.niko-0.0.1-SNAPSHOT.jar pcb442 0.1 2 0.1 0.9 0.96 30 179500 1462343763254 | grep 'Seed for' >> solutions.txt
echo "$(date) - rat783" 
nice -n -20 taskset 0x1 java -jar algo.cup.niko-0.0.1-SNAPSHOT.jar rat783 0.1 2 0.1 0.985 0 15 179500 1462344733968 | grep 'Seed for' >> solutions.txt
echo "$(date) - u1060" 
nice -n -20 taskset 0x1 java -jar algo.cup.niko-0.0.1-SNAPSHOT.jar u1060 0.1 2 0.1 0.996 0 15 179500 1462378695007 | grep 'Seed for' >> solutions.txt
echo "$(date) - fl1577" 
nice -n -20 taskset 0x1 java -jar algo.cup.niko-0.0.1-SNAPSHOT.jar fl1577 2 0.1 0.1 0.995 0 15 179500 1462116966413 | grep 'Seed for' >> solutions.txt
