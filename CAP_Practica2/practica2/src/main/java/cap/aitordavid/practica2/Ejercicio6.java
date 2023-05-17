package cap.aitordavid.practica2;


import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.print.DocFlavor.READER;


public class Ejercicio6 {

    public Ejercicio6(){}

    public void run(){

        this.initCloudSim();

        this.createDataCenter();

        List<DatacenterBroker> brokers = this.createResources();

        this.simulate();
        
        for(int idx = 0 ; idx < brokers.size(); idx++){            
        this.printCloudletsResults(brokers.get(idx));
        }

    }

    private void initCloudSim(){
        Log.printLine(">> Initializing cloudsim...");
        int num_user = 1; // number of cloud users
        Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.
        boolean traceFlag = false; // trace events
        CloudSim.init(num_user, calendar, traceFlag);
        Log.printLine(">> Cloudsim ready!");
    }

    private void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
                + "Data center ID" + indent + "VM ID" + indent + "Time" + indent
                + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId()
                        + indent + indent + indent + cloudlet.getVmId()
                        + indent + indent
                        + dft.format(cloudlet.getActualCPUTime()) + indent
                        + indent + dft.format(cloudlet.getExecStartTime())
                        + indent + indent
                        + dft.format(cloudlet.getFinishTime()));
            }
        }
        
    }

    private void createDataCenter(){

        final int NUMERO_HOSTS = 20; // Queremos 20 hosts

       
        List<Host> listaHosts = new ArrayList<Host>();
        List<Pe>[] listaCPUs = new List[NUMERO_HOSTS];
        Host[] host = new Host[NUMERO_HOSTS];

        for (int i = 0; i < 20; i++) { //HOST 
            listaCPUs[i] = new ArrayList<Pe>();
            if (i < 16) { // Host de tipo 1
                listaCPUs[i].add(new Pe(1, new PeProvisionerSimple(2000)));
                listaCPUs[i].add(new Pe(2, new PeProvisionerSimple(2000)));
                host[i] = new Host(i,
                            new RamProvisionerSimple(8192),              //asignación de memoria RAM
                            new BwProvisionerSimple(10000),        // Asignación de BW
                            1000000,                             // Asignación de almacenamiento
                            listaCPUs[i],                               // Asignación de CPUS
                            new VmSchedulerSpaceShared(listaCPUs[i]));   //Asignación de política
                            listaHosts.add(host[i]);
            }else{ // Host de tipo 2
                listaCPUs[i].add(new Pe(1, new PeProvisionerSimple(2400)));
                listaCPUs[i].add(new Pe(1, new PeProvisionerSimple(2400)));
                listaCPUs[i].add(new Pe(1, new PeProvisionerSimple(2400)));
                listaCPUs[i].add(new Pe(1, new PeProvisionerSimple(2400)));
                host[i] = new Host(i,
                            new RamProvisionerSimple(24576),              //asignación de memoria RAM
                            new BwProvisionerSimple(10000),        // Asignación de BW
                            2000000,                             // Asignación de almacenamiento
                            listaCPUs[i],                               // Asignación de CPUS
                            new VmSchedulerSpaceShared(listaCPUs[i]));   //Asignación de política
                            listaHosts.add(host[i]);
            }
            
        }
        

        String arquitectura = "x86";
        String so = "Linux";
        String vmm = "Xen";
        String nombre = "Datacenter_0";
        double zonaHoraria = 1.0; //Horario estándar (UTC+1): 1.0
        double costePorSeg = 0.01;
        double costePorMem = 0.01;
        double costePorAlm = 0.01;
        double costePorBw = 0.01;

        DatacenterCharacteristics caracteristicas  = new DatacenterCharacteristics(
                arquitectura, so, vmm,
                listaHosts, zonaHoraria, costePorSeg, costePorMem,
                costePorAlm, costePorBw);

        Datacenter centroDeDatos = null;
        try {
            centroDeDatos = new Datacenter(nombre, caracteristicas,
                    new VmAllocationPolicySimple(listaHosts),
                    new LinkedList<Storage>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<DatacenterBroker> createResources() {
        List<DatacenterBroker> brokers = new ArrayList<>();

        for (int userId = 0; userId < 3; userId++) {
            DatacenterBroker broker = null;
            try {
                broker = new DatacenterBroker("broker");
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.printLine(">> ERROR creating broker");
            }
    
            List<Vm> virtualMachines = new ArrayList<Vm>();

            if (userId == 0){ //Usuario 1
                
                for (int idx = 0; idx < 8; idx++) {
                    virtualMachines.add(new Vm( // TIPO A
                                virtualMachines.size(), // ID de la máquina virtual
                                broker.getId(), // ID del broker
                                2400, // MIPS (millones de instrucciones por segundo)
                                1, // Número de procesadores requeridos
                                3072, // Cantidad de RAM en MB
                                1000, // Cantidad de BW (ancho de banda) en Mbps
                                122880, // Espacio de almacenamiento en MB 
                                "Xen", // Tipo de hipervisor utilizado para virtualizar la máquina virtual
                                new CloudletSchedulerSpaceShared() // Tipo de planificador de tareas
                        ));
                }
            }else if (userId == 1){ // Usuario 2
                for (int idx = 0; idx < 16; idx++) {
                    virtualMachines.add(new Vm( // TIPO B
                                virtualMachines.size(), // ID de la máquina virtual
                                broker.getId(), // ID del broker
                                2000, // MIPS (millones de instrucciones por segundo)
                                1, // Número de procesadores requeridos
                                2048, // Cantidad de RAM en MB
                                1000, // Cantidad de BW (ancho de banda) en Mbps
                                81920, // Espacio de almacenamiento en MB 
                                "Xen", // Tipo de hipervisor utilizado para virtualizar la máquina virtual
                                new CloudletSchedulerSpaceShared() // Tipo de planificador de tareas
                        ));
                }
            }else{
                for (int idx = 0; idx < 24; idx++) {
                    virtualMachines.add(new Vm( // TIPO C
                                virtualMachines.size(), // ID de la máquina virtual
                                broker.getId(), // ID del broker
                                1800, // MIPS (millones de instrucciones por segundo)
                                1, // Número de procesadores requeridos
                                1024, // Cantidad de RAM en MB
                                1000, // Cantidad de BW (ancho de banda) en Mbps
                                61440, // Espacio de almacenamiento en MB 
                                "Xen", // Tipo de hipervisor utilizado para virtualizar la máquina virtual
                                new CloudletSchedulerSpaceShared() // Tipo de planificador de tareas
                        ));

                }
                
            }
            broker.submitVmList(virtualMachines);
            brokers.add(userId, broker);  
            }
               
        return brokers;
    }

    private void simulate(){
        Log.printLine(">> Iniciando simulación...");
        CloudSim.startSimulation();
        Log.printLine(">> Simulación en curso...");
        CloudSim.stopSimulation();
        Log.printLine(">> Simulación finalizada.");
    }

    private void printCloudletsResults(DatacenterBroker broker) {
       // this.printCloudletList(broker.getCloudletReceivedList());
    }
}
