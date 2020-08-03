# Dijkstra Shortest Paths distance algorithm
   First Solution based on spark graphx library : 
   
   There are many variations of Dijkstra’s algorithm, including versions for directed ver- sus undirected graphs. The implementation in the project is geared toward directed graphs.
   
   References: Spark GraphX in Action (Michael S. Malak and Robin East)

   Second Solution based on Colection:
    
   This solution is basic implementation of Dijkstra algorithm to calculate min paths of the triangle of numbers

            
# How to build

    The project can be build using sbt-package which creates a jar named suprnation.jar under target/
    
        sbt clean package
    
    All necessary files located in resources directory: 
    
        edge.txt & vertex.txt files source of first solution and graphresult.txt is an outputfile     
        basictree.txt file is source for second solution, basicresult.txt is an outputfile 
        