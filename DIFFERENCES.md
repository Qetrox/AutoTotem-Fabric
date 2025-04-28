# Differences from AutoTotem-Fabric (by Developer-Mike)

This document highlights the key differences between this project and the original [AutoTotem-Fabric](https://github.com/Developer-Mike/Autototem-Fabric) by Developer-Mike.

---

## 🛠 Core Modifications

- **Configurable Behavior**  
  Added a full configuration system (`AutototemConfigManager`) to control features like effect checking, packet delay, and randomization.

- **Delay and Randomization**  
  Introduced a system to delay packet sending and optionally add random delays to mimic more natural behavior.

- **Advanced Slot Management**  
  Inventory scanning prioritizes inventory slots (9-35) over hotbar slots (0-8) when selecting a spare Totem of Undying.

- **Improved Packet Handling**  
  Packet sending is now delayed based on configuration instead of being instant. This reduces potential detection by anti-cheat systems and improves flexibility.

- **Enhanced Restocking Logic**  
  When restocking from inventory, a simulated item pickup and placement (via `ClickSlotC2SPacket`) is used to swap the Totem into the offhand slot cleanly.

---

## 📋 License Clarifications

- Portions of the file `src/main/java/net/qlient/autototem/mixin/TotemMixin.java` are derived from the original AutoTotem-Fabric under the MIT License.
- All additional features, configuration systems, packet timing adjustments, and inventory handling improvements are © 2025 Qetrox. All rights reserved.

For full legal information, please refer to the [LICENSE](./LICENSE) file.

---

## 🤝 Credits

- [Developer-Mike](https://github.com/Developer-Mike) for the original AutoTotem-Fabric mod.
- [Qetrox](https://github.com/QetroxLOL) for extensive modifications and expansions.

