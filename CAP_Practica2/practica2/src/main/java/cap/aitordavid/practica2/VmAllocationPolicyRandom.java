package cap.aitordavid.practica2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;


public class VmAllocationPolicyRandom extends VmAllocationPolicySimple {
    public VmAllocationPolicyRandom(List<? extends Host> list) {
        super(list);
    }

<<<<<<< HEAD
    @Override
    public boolean allocateHostForVm(Vm vm) {
        int requiredPes = vm.getNumberOfPes();
        boolean result = false;
        int tries = 0;
        List<Integer> freePesTmp = new ArrayList<Integer>();
        for (Integer freePes : getFreePes()) {
            freePesTmp.add(freePes);
        }
        if (!getVmTable().containsKey(vm.getUid())) { // if this vm was not created
            do { // we still trying until we find a host or until we try all of them
                int moreFree = Integer.MIN_VALUE;
                int idx = -1;
                // we want the host with less pes in use
                for (int i = 0; i < freePesTmp.size(); i++) {
                    if (freePesTmp.get(i) > moreFree) {
                        moreFree = freePesTmp.get(i);
                        idx = i;
                    }
                }
                Host host = getHostList().get(idx);
                result = host.vmCreate(vm);
                if (result) { // if vm was successfully created in the host
                    getVmTable().put(vm.getUid(), host);
                    getUsedPes().put(vm.getUid(), requiredPes);
                    getFreePes().set(idx, getFreePes().get(idx) - requiredPes);
                    Log.printLine("Maquina virtual #" + vm.getId() + " creada en el host #" + host.getId());
                    result = true;
                    break;
                } else {
                    freePesTmp.set(idx, Integer.MIN_VALUE);
                }
                tries++;
            } while (!result && tries < getFreePes().size());
        }
        /* 
        if (!result) {
            Log.printLine("Maquina virtual #" + vm.getId() + " no pudo ser creada en ningún host");
        }
        */
        return result;
=======
@Override
public boolean allocateHostForVm(Vm vm) {
    int requiredPes = vm.getNumberOfPes();
    boolean result = false;
    int tries = 0;
    List<Integer> freePesTmp = new ArrayList<Integer>();
    for (Integer freePes : getFreePes()) {
        freePesTmp.add(freePes);
>>>>>>> 6b450dd42326ef4354e6075919daae712db88d90
    }

    if (!getVmTable().containsKey(vm.getUid())) { // if this vm was not created
        Set<Integer> indicesSeleccionados = new HashSet<>(); // Almacenamos host ya seleccionados

        do { // we still trying until we find a host or until we try all of them
            int idx;
            // Seleccionamos un host aleatorio que no haya sido seleccionado previamente
            do {
                idx = (int)(Math.random()*(getHostList().size()));
            } while (indicesSeleccionados.contains(idx));

            indicesSeleccionados.add(idx);
            Host host = getHostList().get(idx);
            Log.printLine("Intentamos Crear la VM: "+vm.getId()+"  Con el Host: "+ host.getId());

            result = host.vmCreate(vm);
            if (result) { // if vm was successfully created in the host
                getVmTable().put(vm.getUid(), host);
                getUsedPes().put(vm.getUid(), requiredPes);
                getFreePes().set(idx, getFreePes().get(idx) - requiredPes);
                Log.printLine("Máquina virtual #" + vm.getId() + " creada en el host #" + host.getId());
                result = true;
                break;
            } else {
                freePesTmp.set(idx, Integer.MIN_VALUE); // Si el host seleccionado no permite la creación de la máquina virtual, se marca su número de PEs como Integer.MIN_VALUE en la lista freePesTmp, evitando repeticiones
                Log.printLine("No se pudo conseguir, intentando otro aleatorio...");
            }
            tries++;
        } while (!result && tries < getFreePes().size());
    }

    return result;
}

}