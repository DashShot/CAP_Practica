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


public class Ejercicio2 {

    public Ejercicio2(){}

    public void run(){

        this.initCloudSim();

        this.createDataCenter();

        DatacenterBroker broker = this.createResources();

        this.simulate();

        this.printCloudletsResults(broker);

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
        List<Pe> listaCPUs = new ArrayList<Pe>();
        int mips = 500;
        listaCPUs.add(new Pe(0, new PeProvisionerSimple(mips)));
        listaCPUs.add(new Pe(1, new PeProvisionerSimple(mips)));
        int hostId = 0;
        int ram = 4096;
        long almacenamiento = 20000;
        long anchoBanda = 1000;

        Host host = new Host(hostId, 
                        new RamProvisionerSimple(ram), //asignar y gestionar la memoria RAM
                        new BwProvisionerSimple(anchoBanda), //asignar y gestionar el ancho de banda de red
                        almacenamiento, // Especifica la capacidad de almacenamiento total del host
                        listaCPUs,  //Es una lista que contiene las instancias de la clase Pe (Processing Element), que representan los procesadores físicos del host
                        new VmSchedulerSpaceShared(listaCPUs)); //programar y gestionar la ejecución de las máquinas virtuales en el host
                            //VmSchedulerTimeShared

        List<Host> listaHosts = new ArrayList<Host>();
        listaHosts.add(host);

        String arquitectura = "x86";
        String so = "Linux";
        String vmm = "Xen";
        String nombre = "Datacenter_0";
        double zonaHoraria = 4.0;
        double costePorSeg = 0.01;
        double costePorMem = 0.01;
        double costePorAlm = 0.003;
        double costePorBw = 0.005;

        DatacenterCharacteristics caracteristicas = new DatacenterCharacteristics(arquitectura,
                so, vmm, listaHosts, zonaHoraria, costePorSeg,
                costePorMem, costePorAlm, costePorBw);

        Datacenter centroDeDatos = null;
        try {
            centroDeDatos = new Datacenter(nombre, caracteristicas,
                    new VmAllocationPolicySimple(listaHosts),
                    new LinkedList<Storage>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private DatacenterBroker createResources() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("broker");
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.printLine(">> ERROR creating broker");
        }

        List<Vm> virtualMachines = new ArrayList<Vm>();
        for (int idx = 0; idx < 2; idx++) {
            virtualMachines.add(new Vm(
                                        virtualMachines.size(),      // ID de la máquina virtual
                                        broker.getId(),              // ID del broker
                                        200,                         // MIPS (millones de instrucciones por segundo)
                                        2,                           // Número de procesadores requeridos
                                        1024,                        // Cantidad de RAM en MB
                                        100,                         // Cantidad de BW (ancho de banda) en Mbps
                                        6144,                        // Espacio de almacenamiento en MB (6 GB = 6144 MB)
                                        "Xen",                       // Tipo de hipervisor utilizado para virtualizar la máquina virtual
                                        new CloudletSchedulerTimeShared()  // Tipo de planificador de tareas
                                    ));
        }
        

        broker.submitVmList(virtualMachines);

        List<Cloudlet> cloudlets = new ArrayList<Cloudlet>();

        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int idx1 = 0 ; idx1 < 12 ; idx1++){
            Cloudlet cloudlet = new Cloudlet(
                                cloudlets.size(),    // ID del cloudlet
                                10000,               // Longitud de instrucción
                                1,                   // Número de núcleos requeridos
                                2000000,             // Tamaño de datos de entrada (B)
                                2500000,             // Tamaño de datos de salida  (B)
                                utilizationModel,    // Modelo de utilización de CPU
                                utilizationModel,    // Modelo de utilización de RAM
                                utilizationModel     // Modelo de utilización de ancho de banda
                            );
                    cloudlet.setUserId(broker.getId());
                    cloudlets.add(cloudlet);
                }

        

        broker.submitCloudletList(cloudlets);

        return broker;
    }

    private void simulate(){
        Log.printLine(">> Iniciando simulación...");
        CloudSim.startSimulation();
        Log.printLine(">> Simulación en curso...");
        CloudSim.stopSimulation();
        Log.printLine(">> Simulación finalizada.");
    }

    private void printCloudletsResults(DatacenterBroker broker) {
        this.printCloudletList(broker.getCloudletReceivedList());
    }
}