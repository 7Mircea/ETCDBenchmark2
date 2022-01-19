# etapa 4

# How to run

In order to run this example you have to execute :  
```
cd
etcd
```
First in order to go to home folder for current user and the second for starting a etcd store.
The program uses at least : 121_555_200B = 121.5MB(aprox) of RAM.
In order to compile and run the program.  
```
gradle clean  
gradle build  
gradle run  
```

# Testing environment used by author

Linux 20.04, Intel i7, 16GB RAM, java 17.

# Testing

Before you run the program you have to write this in a terminal:  
```
etcd  
```
To check if the etcd store is working correctly:  
```
etcdctl put cheie valoare  
etcdctl get cheie  
```
If you want to delete:  
```
etcdctl del cheie  
```

## Configuration

You could configure the scale factor(how many lines to be inserted) by changing scaleFactorArr, the number of times the insert operation is repetead by changing repeatNr.  
You should definitly change the path of the file with values to be inserted.

# Result

## Inseration

Scalation factor : 10000  
Inseration time:8039.0s  
Inseration time:6608.0s  
Inseration time:6445.0s  
Inseration time:6407.0s  
Inseration time:6320.0s  
Mean : 6763.8. Std dev: 644.4077591090909  

Scalation factor : 100000  
Inseration time:63622.0s  
Inseration time:66698.0s  
Inseration time:68952.0s  
Inseration time:68697.0s  
Inseration time:67464.0s  
Mean : 67086.6. Std dev: 1916.7680715203912  

Scalation factor : 200000  
Inseration time:133955.0s  
Inseration time:129730.0s  
Inseration time:131649.0s  
Inseration time:131543.0s  
Inseration time:130715.0s  
Mean : 131518.4. Std dev: 1399.8933673676722  
  
Scalation factor : 400000  
Inseration time:262260.0s  
Inseration time:268585.0s  
Inseration time:267583.0s  
Inseration time:265803.0s  
Inseration time:266425.0s  
Mean : 266131.2. Std dev: 2159.1239334507873  

Scalation factor : 405184  
Inseration time:267925.0s  
Inseration time:267258.0s  
Inseration time:268205.0s  
Inseration time:270758.0s  
Inseration time:271942.0s  
Mean : 269217.6. Std dev: 1807.284880698115  

# Updating
Scalation factor : 10000
Update time:15.0 ms
Update time:2.0 ms
Update time:1.0 ms
Update time:2.0 ms
Update time:1.0 ms
Update time:1.0 ms
Update time:1.0 ms
Update time:1.0 ms
Update time:2.0 ms
Update time:1.0 ms
Mean : 2.7 ms. Std dev: 4.124318125460256




