import {api} from "../utils/api";

export async function getVeterinarians() {
  try {
    const {data} = await api.get("/veterinarians/get-all-veterinarians");
    console.log("The result ", data);
    return data;
  } catch (error) {
    throw error;
  }
}

export async function findAvailableVeterinarians(searchParams) {
  try {
    const queryParams = new URLSearchParams(searchParams);
    const {data} = await api.get(
      `/veterinarians/search-veterinarian?${queryParams}`
    );
    return data;
  } catch (error) {
    throw error;
  }
}

export const getAllSpecializations = async () => {
  try {
    const {data} = await api.get("/veterinarians/vet/get-all-specialization");
    return data;
  } catch (error) {
    throw error;
  }
};
