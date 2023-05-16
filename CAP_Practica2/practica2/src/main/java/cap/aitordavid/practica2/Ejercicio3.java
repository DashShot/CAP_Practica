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

public class Ejercicio3 {

    public Ejercicio3() {
    }

    public void run() {

        this.initCloudSim();

        this.createDataCenter();

        List<DatacenterBroker> brokers = this.createResources();

        this.simulate();
        
        for(int idx = 0 ; idx < brokers.size(); idx++){            
        this.printCloudletsResults(brokers.get(idx));
        }
    }

    private void initCloudSim() {
        Log.printLine(">> Initializing cloudsim...");
        int num_user = 10; // number of cloud users
        Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date
                                                    // and time.
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

    private void createDataCenter() {
        List<Pe> listaCPUs = new ArrayList<Pe>();
        int mips = 1200;
        listaCPUs.add(new Pe(0, new PeProvisionerSimple(mips)));
        listaCPUs.add(new Pe(1, new PeProvisionerSimple(mips)));
        listaCPUs.add(new Pe(2, new PeProvisionerSimple(mips)));
        listaCPUs.add(new Pe(3, new PeProvisionerSimple(mips)));

        int ram = 24576; // 24 GB
        long almacenamiento = 2000000; // 2 TB
        long anchoBanda = 10000; // 10 Gbps

        final int NUMERO_HOSTS = 5; // Queremos 5 hosts
        Host[] host = new Host[NUMERO_HOSTS];
        List<Host> listaHosts = new ArrayList<Host>();
        for (int i = 0; i < NUMERO_HOSTS; i++) {
            host[i] = new Host(
                    i, new RamProvisionerSimple(ram),
                    new BwProvisionerSimple(anchoBanda), almacenamiento,
                    listaCPUs, new VmSchedulerSpaceShared(listaCPUs));
            listaHosts.add(host[i]);
        }

        String arquitectura = "x86";
        String so = "Linux";
        String vmm = "Xen";
        String nombre = "Datacenter_0";
        double zonaHoraria = 2.0;
        double costePorSeg = 0.01;
        double costePorMem = 0.005;
        double costePorAlm = 0.003;
        double costePorBw = 0.005;

        DatacenterCharacteristics caracteristicas = new DatacenterCharacteristics(arquitectura, so, vmm,
                listaHosts, zonaHoraria, costePorSeg,
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

    private List<DatacenterBroker> createResources() {
        List<DatacenterBroker> brokers = new ArrayList<>();

        for (int userId = 0; userId < 10; userId++) {
            DatacenterBroker broker = null;
            try {
                broker = new DatacenterBroker("broker");
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.printLine(">> ERROR creating broker");
            }
            

            List<Vm> virtualMachines = new ArrayList<Vm>();
            for (int idx = 0; idx < 3; idx++) {
                virtualMachines.add(new Vm(
                        virtualMachines.size(), // ID de la máquina virtual
                        broker.getId(), // ID del broker
                        600, // MIPS (millones de instrucciones por segundo)
                        2, // Número de procesadores requeridos
                        4096, // Cantidad de RAM en MB
                        1000, // Cantidad de BW (ancho de banda) en Mbps
                        20480, // Espacio de almacenamiento en MB (20 GB = 20480 MB)
                        "Xen", // Tipo de hipervisor utilizado para virtualizar la máquina virtual
                        new CloudletSchedulerSpaceShared() // Tipo de planificador de tareas
                ));
            }

            broker.submitVmList(virtualMachines);

            List<Cloudlet> cloudlets = new ArrayList<Cloudlet>();

            UtilizationModel utilizationModel = new UtilizationModelFull();

            for (int idx1 = 0; idx1 < 15; idx1++) {
                Cloudlet cloudlet = new Cloudlet(
                        cloudlets.size(), // ID del cloudlet
                        45000, // Longitud de instrucción
                        1, // Número de núcleos requeridos
                        1000000, // Tamaño de datos de entrada (B)
                        1500000, // Tamaño de datos de salida (B)
                        utilizationModel, // Modelo de utilización de CPU
                        utilizationModel, // Modelo de utilización de RAM
                        utilizationModel // Modelo de utilización de ancho de banda
                );
                cloudlet.setUserId(broker.getId());
                cloudlets.add(cloudlet);
            }

            broker.submitCloudletList(cloudlets);
            brokers.add(broker);
        }
        return brokers;
    }

    private void simulate() {
            
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