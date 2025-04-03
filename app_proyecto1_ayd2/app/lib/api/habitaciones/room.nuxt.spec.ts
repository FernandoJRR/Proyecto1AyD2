import { describe, it, expect, vi, beforeAll, beforeEach } from "vitest";

// Mock de $api
const mockApi = vi.fn();

vi.doMock("~/utils/plainFetch", () => ({
  $api: mockApi,
}));

let getAllRooms: any;
let getAllAvailableRooms: any;
let createRoom: any;
let updateRoom: any;
let toggleRoomAvailability: any;
let getRoomByNumber: any;
let getRoomById: any;

beforeAll(async () => {
  const module = await import("~/lib/api/habitaciones/room");
  getAllRooms = module.getAllRooms;
  getAllAvailableRooms = module.getAllAvailableRooms;
  createRoom = module.createRoom;
  updateRoom = module.updateRoom;
  toggleRoomAvailability = module.toggleRoomAvailability;
  getRoomByNumber = module.getRoomByNumber;
  getRoomById = module.getRoomById;
});

beforeEach(() => {
  vi.clearAllMocks();
});

describe("Rooms API Utilities", () => {
  it("getAllRooms llama a $api correctamente", async () => {
    const mockResponse = [{ id: "1", number: "101" }];
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getAllRooms();

    expect(mockApi).toHaveBeenCalledWith("/v1/rooms/all");
    expect(result).toEqual(mockResponse);
  });

  it("getAllAvailableRooms llama a $api correctamente", async () => {
    const mockResponse = [{ id: "2", number: "102" }];
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getAllAvailableRooms();

    expect(mockApi).toHaveBeenCalledWith("/v1/rooms/all/available");
    expect(result).toEqual(mockResponse);
  });

  it("createRoom llama a $api con el body correcto", async () => {
    const payload = {
      number: "103",
      dailyMaintenanceCost: 50,
      dailyPrice: 120,
    };
    const mockResponse = { id: "3", ...payload };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await createRoom(payload);

    expect(mockApi).toHaveBeenCalledWith("/v1/rooms", {
      method: "POST",
      body: payload,
    });
    expect(result).toEqual(mockResponse);
  });

  it("updateRoom llama a $api con el ID y datos correctos", async () => {
    const roomId = "4";
    const payload = {
      number: "104",
      dailyMaintenanceCost: 60,
      dailyPrice: 150,
    };
    const mockResponse = { id: roomId, ...payload };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await updateRoom(roomId, payload);

    expect(mockApi).toHaveBeenCalledWith(`/v1/rooms/${roomId}`, {
      method: "PATCH",
      body: payload,
    });
    expect(result).toEqual(mockResponse);
  });

  it("toggleRoomAvailability llama a $api con el ID correcto", async () => {
    const roomId = "5";
    const mockResponse = { id: roomId, status: "INACTIVE" };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await toggleRoomAvailability(roomId);

    expect(mockApi).toHaveBeenCalledWith(
      `/v1/rooms/${roomId}/toggle-availability`,
      { method: "PATCH" }
    );
    expect(result).toEqual(mockResponse);
  });

  it("getRoomByNumber llama a $api con el número correcto", async () => {
    const number = "105";
    const mockResponse = { id: "6", number };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getRoomByNumber(number);

    expect(mockApi).toHaveBeenCalledWith(`/v1/rooms/${number}/by-number`);
    expect(result).toEqual(mockResponse);
  });

  it("getRoomById llama a $api con el ID correcto", async () => {
    const id = "7";
    const mockResponse = { id, number: "106" };
    mockApi.mockResolvedValueOnce(mockResponse);

    const result = await getRoomById(id);

    expect(mockApi).toHaveBeenCalledWith(`/v1/rooms/${id}`);
    expect(result).toEqual(mockResponse);
  });
});
