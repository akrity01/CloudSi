import tkinter as tk
from tkinter import messagebox, ttk, scrolledtext
import random
class VM:
    def __init__(self, vm_id):
        self.vm_id = vm_id
        self.data_received = 0

    def send_data(self, other_vm, amount):
        other_vm.receive_data(amount)

    def receive_data(self, amount):
        self.data_received += amount

class CloudSimulator:
    def __init__(self, num_vms):
        self.vms = [VM(i) for i in range(num_vms)]
    def simulate_exchange(self):
        log = []
        for _ in range(10):  # Simulate 10 exchanges
            sender, receiver = random.sample(self.vms, 2)
            amount = random.randint(10, 100)
            sender.send_data(receiver, amount)
            log.append((sender.vm_id, receiver.vm_id, amount))
        return log

    def get_data_summary(self):
        return [(vm.vm_id, vm.data_received) for vm in self.vms]
    
def run_simulation():      #GUI
    try:
        num_vms = int(vm_entry.get())
        if num_vms < 2:
            raise ValueError("Enter at least 2 VMs.")
        simulator = CloudSimulator(num_vms)
        logs = simulator.simulate_exchange()
        summary = simulator.get_data_summary()
        output_text.configure(state='normal')
        output_text.delete("1.0", tk.END)
        output_text.insert(tk.END, "Inter-VM Data Exchange Log:\n\n", "header")
        for s, r, a in logs:
            output_text.insert(tk.END, f"VM{s} ➡ VM{r} | Data Sent: {a} units\n", "log")
        output_text.insert(tk.END, "\nFinal Data Received Summary:\n\n", "header")
        for vm_id, total in summary:
            output_text.insert(tk.END, f"VM{vm_id} {total} units\n", "summary")
        output_text.configure(state='disabled')
    except ValueError as ve:
        messagebox.showerror("Input Error", str(ve))
    except Exception as e:
        messagebox.showerror("Simulation Error", str(e))

#GUI 
root = tk.Tk()
root.title("CloudSim: Inter-VM Data Exchange Simulator")
root.geometry("650x550")
root.resizable(False, False)
root.configure(bg="#e6f0ff")
# Header
tk.Label(root, text="☁ CloudSim Simulator", font=("Helvetica", 20, "bold"), bg="#e6f0ff", fg="#003366").pack(pady=10)
tk.Label(root, text="Simulate data transfer between virtual machines", font=("Helvetica", 12), bg="#e6f0ff", fg="#333333").pack()
# Input Frame
input_frame = tk.Frame(root, bg="#e6f0ff")
input_frame.pack(pady=15)
tk.Label(input_frame, text="Number of Virtual Machines:", font=("Helvetica", 12), bg="#e6f0ff").grid(row=0, column=0, padx=5, sticky="e")
vm_entry = ttk.Entry(input_frame, width=10)
vm_entry.grid(row=0, column=1, padx=5)
vm_entry.insert(0, "5")
# Run Button
run_btn = ttk.Button(root, text="▶ Run Simulation", command=run_simulation)
run_btn.pack(pady=10)
# Output Text Box
output_text = scrolledtext.ScrolledText(root, width=75, height=20, wrap=tk.WORD, font=("Consolas", 10), state='disabled', bg="#f7faff")
output_text.pack(padx=15, pady=10)
# Text formatting
output_text.tag_configure("header", font=("Helvetica", 12, "bold"), foreground="#003366")
output_text.tag_configure("log", foreground="#004d99")
output_text.tag_configure("summary", foreground="#006600")
# Footer
tk.Label(root, text="Developed by Cloud Stack Innovators", font=("Helvetica", 9), bg="#e6f0ff", fg="#666666").pack(pady=5)

root.mainloop()
