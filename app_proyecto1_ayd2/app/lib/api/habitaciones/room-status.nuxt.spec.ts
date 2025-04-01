import { describe, it, expect } from "vitest";
import { RoomStatus } from "~/lib/api/habitaciones/room-status";

describe("RoomStatus Enum", () => {
  it('debe tener la opción "AVAILABLE"', () => {
    expect(RoomStatus.AVAILABLE).toBe("Disponible");
  });

  it('debe tener la opción "OCCUPIED"', () => {
    expect(RoomStatus.OCCUPIED).toBe("Ocupado");
  });

  it('debe tener la opción "OUT_OF_SERVICE"', () => {
    expect(RoomStatus.OUT_OF_SERVICE).toBe("Fuera de servicio");
  });

  it("debe contener exactamente 3 valores", () => {
    const values = Object.values(RoomStatus);
    expect(values).toEqual(["Disponible", "Ocupado", "Fuera de servicio"]);
    expect(values.length).toBe(3);
  });
});
