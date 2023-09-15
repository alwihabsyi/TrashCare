package com.upnvjt.trashcare.data.tacycle

sealed class TrashCategory(val category: String) {

    object Plastik: TrashCategory("Plastik")
    object LimbahDapur: TrashCategory("Limbah Dapur")
    object SerbukKayu: TrashCategory("Serbuk Kayu")
    object Kaca: TrashCategory("Kaca")
    object KertasKardus: TrashCategory("Kertas/Kardus")


}
