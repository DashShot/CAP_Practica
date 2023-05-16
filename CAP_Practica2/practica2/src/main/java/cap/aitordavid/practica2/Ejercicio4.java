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


public class Ejercicio4 {

    public Ejercicio4(){}

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
        List<Pe> processorElements = new ArrayList<Pe>();
        processorElements.add(new Pe(0, new PeProvisionerSimple(2000)));
        processorElements.add(new Pe(1, new PeProvisionerSimple(2000)));

        List<Host> hosts = new ArrayList<Host>();
        hosts.add(new Host(
                hosts.size(),
                new RamProvisionerSimple(8000),
                new BwProvisionerSimple(1000),
                1000000,
                processorElements,
                new VmSchedulerTimeShared(processorElements)
        ));
        hosts.add(new Host(
                hosts.size(),
                new RamProvisionerSimple(16000),
                new BwProvisionerSimple(1000),
                2000000,
                processorElements,
                new VmSchedulerTimeShared(processorElements)
        ));

        LinkedList<Storage> storageList = new LinkedList<Storage>();

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                "x86", "Linux", "Xen",
                hosts, 1.0, 0.02, 0.01,
                0.001, 0.001);

        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter("datacenterEjemplo", characteristics,
                    new VmAllocationPolicySimple(hosts),
                    storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine(">> ERROR creating datacenter");
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
        for (int idx = 0; idx < 4; idx++) {
            virtualMachines.add(new Vm(virtualMachines.size(), broker.getId(),
                    400, 1, 2000, 100,
                    12000, "Xen", new CloudletSchedulerTimeShared()));
        }

        broker.submitVmList(virtualMachines);

        List<Cloudlet> cloudlets = new ArrayList<Cloudlet>();

        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet cloudlet1 =
                new Cloudlet(cloudlets.size(), 20000,
                        1,
                        1000000, 1500000,
                        utilizationModel,
                        utilizationModel,
                        utilizationModel);
        cloudlet1.setUserId(broker.getId());
        cloudlets.add(cloudlet1);

        Cloudlet cloudlet2 =
                new Cloudlet(cloudlets.size(), 30000,
                        1,
                        2000000, 2200000,
                        utilizationModel,
                        utilizationModel,
                        utilizationModel);
        cloudlet2.setUserId(broker.getId());
        cloudlets.add(cloudlet2);

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
