/*package com.example.mapsapp.data
//siguein
import com.example.mapsapp.data.model.Marcador
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class MarcadorRepository(val client: SupabaseClient) {


    suspend fun insertMarcador(marcador: Marcador){
        client.from("Marcador").insert(marcador)
    }


    suspend fun getAllMarcadores(): List<Marcador> {
        return client.from("Marcador").select().decodeList<Marcador>()
       /* return client
            .from("Marcador")
            .select( Columns.raw("nombre, descripcion, latitud, longitud, image_url"))
            .decodeList<Marcador>()*/
    }

    suspend fun getMarcador(id: String): Marcador {
        return client.from("Marcador").select {
            filter {
                eq("id", id)
            }
        }.decodeSingle<Marcador>()
    }

    suspend fun updateMarcador(id: String, name: String, descripcion:String){
        client.from("Marcador").update({
            set("nombre", name)
        }) { filter { eq("id", id) } }
    }
    suspend fun deleteMarcador(id: String){
        client.from("Marcador").delete{ filter { eq("id", id) } }
    }


}
*/