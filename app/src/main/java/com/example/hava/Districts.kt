package com.example.hava

// Türkiye'deki tüm ilçeler ve bağlı oldukları iller
val districtToProvince: Map<String, String> = mapOf(
    // Adana
    "Seyhan" to "Adana", "Yüreğir" to "Adana", "Çukurova" to "Adana", "Sarıçam" to "Adana",
    "Ceyhan" to "Adana", "Kozan" to "Adana", "İmamoğlu" to "Adana", "Karataş" to "Adana",
    "Karaisalı" to "Adana", "Pozantı" to "Adana", "Yumurtalık" to "Adana", "Tufanbeyli" to "Adana",
    "Feke" to "Adana", "Aladağ" to "Adana", "Saimbeyli" to "Adana",

    // Adıyaman
    "Besni" to "Adıyaman", "Çelikhan" to "Adıyaman", "Gerger" to "Adıyaman", "Gölbaşı" to "Adıyaman",
    "Kahta" to "Adıyaman", "Samsat" to "Adıyaman", "Sincik" to "Adıyaman", "Tut" to "Adıyaman",

    // Afyonkarahisar
    "Başmakçı" to "Afyonkarahisar", "Bayat" to "Afyonkarahisar", "Bolvadin" to "Afyonkarahisar",
    "Çay" to "Afyonkarahisar", "Çobanlar" to "Afyonkarahisar", "Dazkırı" to "Afyonkarahisar",
    "Dinar" to "Afyonkarahisar", "Emirdağ" to "Afyonkarahisar", "Evciler" to "Afyonkarahisar",
    "Hocalar" to "Afyonkarahisar", "İhsaniye" to "Afyonkarahisar", "İscehisar" to "Afyonkarahisar",
    "Kızılören" to "Afyonkarahisar", "Sandıklı" to "Afyonkarahisar", "Sinanpaşa" to "Afyonkarahisar",
    "Sultandağı" to "Afyonkarahisar", "Şuhut" to "Afyonkarahisar",

    // Ağrı
    "Diyadin" to "Ağrı", "Doğubayazıt" to "Ağrı", "Eleşkirt" to "Ağrı", "Hamur" to "Ağrı",
    "Patnos" to "Ağrı", "Taşlıçay" to "Ağrı", "Tutak" to "Ağrı",

    // Amasya
    "Göynücek" to "Amasya", "Gümüşhacıköy" to "Amasya", "Hamamözü" to "Amasya",
    "Merzifon" to "Amasya", "Suluova" to "Amasya", "Taşova" to "Amasya",

    // Ankara
    "Akyurt" to "Ankara", "Altındağ" to "Ankara", "Ayaş" to "Ankara", "Bala" to "Ankara",
    "Beypazarı" to "Ankara", "Çamlıdere" to "Ankara", "Çankaya" to "Ankara", "Çubuk" to "Ankara",
    "Elmadağ" to "Ankara", "Etimesgut" to "Ankara", "Evren" to "Ankara", "Gölbaşı" to "Ankara",
    "Güdül" to "Ankara", "Haymana" to "Ankara", "Kahramankazan" to "Ankara", "Kalecik" to "Ankara",
    "Keçiören" to "Ankara", "Kızılcahamam" to "Ankara", "Mamak" to "Ankara", "Nallıhan" to "Ankara",
    "Polatlı" to "Ankara", "Pursaklar" to "Ankara", "Sincan" to "Ankara", "Şereflikoçhisar" to "Ankara",
    "Yenimahalle" to "Ankara",

    // Antalya
    "Akseki" to "Antalya", "Aksu" to "Antalya", "Alanya" to "Antalya", "Demre" to "Antalya",
    "Döşemealtı" to "Antalya", "Elmalı" to "Antalya", "Finike" to "Antalya", "Gazipaşa" to "Antalya",
    "Gündoğmuş" to "Antalya", "İbradı" to "Antalya", "Kaş" to "Antalya", "Kemer" to "Antalya",
    "Kepez" to "Antalya", "Konyaaltı" to "Antalya", "Korkuteli" to "Antalya", "Kumluca" to "Antalya",
    "Manavgat" to "Antalya", "Muratpaşa" to "Antalya", "Serik" to "Antalya",

    // Artvin
    "Ardanuç" to "Artvin", "Arhavi" to "Artvin", "Borçka" to "Artvin", "Hopa" to "Artvin",
    "Kemalpaşa" to "Artvin", "Murgul" to "Artvin", "Şavşat" to "Artvin", "Yusufeli" to "Artvin",

    // Aydın
    "Bozdoğan" to "Aydın", "Buharkent" to "Aydın", "Çine" to "Aydın", "Didim" to "Aydın",
    "Efeler" to "Aydın", "Germencik" to "Aydın", "İncirliova" to "Aydın", "Karacasu" to "Aydın",
    "Karpuzlu" to "Aydın", "Koçarlı" to "Aydın", "Köşk" to "Aydın", "Kuşadası" to "Aydın",
    "Kuyucak" to "Aydın", "Nazilli" to "Aydın", "Söke" to "Aydın", "Sultanhisar" to "Aydın",
    "Yenipazar" to "Aydın",

    // Balıkesir
    "Altıeylül" to "Balıkesir", "Ayvalık" to "Balıkesir", "Balya" to "Balıkesir", "Bandırma" to "Balıkesir",
    "Bigadiç" to "Balıkesir", "Burhaniye" to "Balıkesir", "Dursunbey" to "Balıkesir", "Edremit" to "Balıkesir",
    "Erdek" to "Balıkesir", "Gömeç" to "Balıkesir", "Gönen" to "Balıkesir", "Havran" to "Balıkesir",
    "İvrindi" to "Balıkesir", "Karesi" to "Balıkesir", "Kepsut" to "Balıkesir", "Manyas" to "Balıkesir",
    "Marmara" to "Balıkesir", "Savaştepe" to "Balıkesir", "Sındırgı" to "Balıkesir", "Susurluk" to "Balıkesir",

    // Bilecik
    "Bozüyük" to "Bilecik", "Gölpazarı" to "Bilecik", "İnhisar" to "Bilecik", "Osmaneli" to "Bilecik",
    "Pazaryeri" to "Bilecik", "Söğüt" to "Bilecik", "Yenipazar" to "Bilecik",

    // Bingöl
    "Adaklı" to "Bingöl", "Genç" to "Bingöl", "Karlıova" to "Bingöl", "Kiğı" to "Bingöl",
    "Solhan" to "Bingöl", "Yayladere" to "Bingöl", "Yedisu" to "Bingöl",

    // Bitlis
    "Adilcevaz" to "Bitlis", "Ahlat" to "Bitlis", "Güroymak" to "Bitlis", "Hizan" to "Bitlis",
    "Mutki" to "Bitlis", "Tatvan" to "Bitlis",

    // Bolu
    "Dörtdivan" to "Bolu", "Gerede" to "Bolu", "Göynük" to "Bolu", "Kıbrıscık" to "Bolu",
    "Mengen" to "Bolu", "Mudurnu" to "Bolu", "Seben" to "Bolu", "Yeniçağa" to "Bolu",

    // Burdur
    "Ağlasun" to "Burdur", "Altınyayla" to "Burdur", "Bucak" to "Burdur", "Çavdır" to "Burdur",
    "Çeltikçi" to "Burdur", "Gölhisar" to "Burdur", "Karamanlı" to "Burdur", "Kemer" to "Burdur",
    "Tefenni" to "Burdur", "Yeşilova" to "Burdur",

    // Bursa
    "Büyükorhan" to "Bursa", "Gemlik" to "Bursa", "Gürsu" to "Bursa", "Harmancık" to "Bursa",
    "İnegöl" to "Bursa", "İznik" to "Bursa", "Karacabey" to "Bursa", "Keles" to "Bursa",
    "Kestel" to "Bursa", "Mudanya" to "Bursa", "Mustafakemalpaşa" to "Bursa", "Nilüfer" to "Bursa",
    "Orhaneli" to "Bursa", "Orhangazi" to "Bursa", "Osmangazi" to "Bursa", "Yenişehir" to "Bursa",
    "Yıldırım" to "Bursa",

    // Çanakkale
    "Ayvacık" to "Çanakkale", "Bayramiç" to "Çanakkale", "Biga" to "Çanakkale", "Bozcaada" to "Çanakkale",
    "Çan" to "Çanakkale", "Eceabat" to "Çanakkale", "Ezine" to "Çanakkale", "Gelibolu" to "Çanakkale",
    "Gökçeada" to "Çanakkale", "Lapseki" to "Çanakkale", "Yenice" to "Çanakkale",

    // Çankırı
    "Atkaracalar" to "Çankırı", "Bayramören" to "Çankırı", "Çerkeş" to "Çankırı", "Eldivan" to "Çankırı",
    "Ilgaz" to "Çankırı", "Kızılırmak" to "Çankırı", "Korgun" to "Çankırı", "Kurşunlu" to "Çankırı",
    "Orta" to "Çankırı", "Şabanözü" to "Çankırı", "Yapraklı" to "Çankırı",

    // Çorum
    "Alaca" to "Çorum", "Bayat" to "Çorum", "Boğazkale" to "Çorum", "Dodurga" to "Çorum",
    "İskilip" to "Çorum", "Kargı" to "Çorum", "Laçin" to "Çorum", "Mecitözü" to "Çorum",
    "Oğuzlar" to "Çorum", "Ortaköy" to "Çorum", "Osmancık" to "Çorum", "Sungurlu" to "Çorum",
    "Uğurludağ" to "Çorum",

    // Denizli
    "Acıpayam" to "Denizli", "Babadağ" to "Denizli", "Baklan" to "Denizli", "Bekilli" to "Denizli",
    "Beyağaç" to "Denizli", "Bozkurt" to "Denizli", "Buldan" to "Denizli", "Çal" to "Denizli",
    "Çameli" to "Denizli", "Çardak" to "Denizli", "Çivril" to "Denizli", "Güney" to "Denizli",
    "Honaz" to "Denizli", "Kale" to "Denizli", "Merkezefendi" to "Denizli", "Pamukkale" to "Denizli",
    "Sarayköy" to "Denizli", "Serinhisar" to "Denizli", "Tavas" to "Denizli",

    // Diyarbakır
    "Bağlar" to "Diyarbakır", "Bismil" to "Diyarbakır", "Çermik" to "Diyarbakır", "Çınar" to "Diyarbakır",
    "Çüngüş" to "Diyarbakır", "Dicle" to "Diyarbakır", "Eğil" to "Diyarbakır", "Ergani" to "Diyarbakır",
    "Hani" to "Diyarbakır", "Hazro" to "Diyarbakır", "Kayapınar" to "Diyarbakır", "Kocaköy" to "Diyarbakır",
    "Kulp" to "Diyarbakır", "Lice" to "Diyarbakır", "Silvan" to "Diyarbakır", "Sur" to "Diyarbakır",
    "Yenişehir" to "Diyarbakır",

    // Edirne
    "Enez" to "Edirne", "Havsa" to "Edirne", "İpsala" to "Edirne", "Keşan" to "Edirne",
    "Lalapaşa" to "Edirne", "Meriç" to "Edirne", "Süloğlu" to "Edirne", "Uzunköprü" to "Edirne",

    // Elazığ
    "Ağın" to "Elazığ", "Alacakaya" to "Elazığ", "Arıcak" to "Elazığ", "Baskil" to "Elazığ",
    "Karakoçan" to "Elazığ", "Keban" to "Elazığ", "Kovancılar" to "Elazığ", "Maden" to "Elazığ",
    "Palu" to "Elazığ", "Sivrice" to "Elazığ",

    // Erzincan
    "Çayırlı" to "Erzincan", "İliç" to "Erzincan", "Kemah" to "Erzincan", "Kemaliye" to "Erzincan",
    "Otlukbeli" to "Erzincan", "Refahiye" to "Erzincan", "Tercan" to "Erzincan", "Üzümlü" to "Erzincan",

    // Erzurum
    "Aşkale" to "Erzurum", "Aziziye" to "Erzurum", "Çat" to "Erzurum", "Hınıs" to "Erzurum",
    "Horasan" to "Erzurum", "İspir" to "Erzurum", "Karaçoban" to "Erzurum", "Karayazı" to "Erzurum",
    "Köprüköy" to "Erzurum", "Narman" to "Erzurum", "Oltu" to "Erzurum", "Olur" to "Erzurum",
    "Palandöken" to "Erzurum", "Pasinler" to "Erzurum", "Pazaryolu" to "Erzurum", "Şenkaya" to "Erzurum",
    "Tekman" to "Erzurum", "Tortum" to "Erzurum", "Uzundere" to "Erzurum", "Yakutiye" to "Erzurum",

    // Eskişehir
    "Alpu" to "Eskişehir", "Beylikova" to "Eskişehir", "Çifteler" to "Eskişehir", "Günyüzü" to "Eskişehir",
    "Han" to "Eskişehir", "İnönü" to "Eskişehir", "Mahmudiye" to "Eskişehir", "Mihalgazi" to "Eskişehir",
    "Mihalıççık" to "Eskişehir", "Odunpazarı" to "Eskişehir", "Sarıcakaya" to "Eskişehir", "Seyitgazi" to "Eskişehir",
    "Sivrihisar" to "Eskişehir", "Tepebaşı" to "Eskişehir",

    // Gaziantep
    "Araban" to "Gaziantep", "İslahiye" to "Gaziantep", "Karkamış" to "Gaziantep", "Nizip" to "Gaziantep",
    "Nurdağı" to "Gaziantep", "Oğuzeli" to "Gaziantep", "Şahinbey" to "Gaziantep", "Şehitkamil" to "Gaziantep",
    "Yavuzeli" to "Gaziantep",

    // Giresun
    "Alucra" to "Giresun", "Bulancak" to "Giresun", "Çamoluk" to "Giresun", "Çanakçı" to "Giresun",
    "Dereli" to "Giresun", "Doğankent" to "Giresun", "Espiye" to "Giresun", "Eynesil" to "Giresun",
    "Görele" to "Giresun", "Güce" to "Giresun", "Keşap" to "Giresun", "Piraziz" to "Giresun",
    "Şebinkarahisar" to "Giresun", "Tirebolu" to "Giresun", "Yağlıdere" to "Giresun",

    // Gümüşhane
    "Kelkit" to "Gümüşhane", "Köse" to "Gümüşhane", "Kürtün" to "Gümüşhane", "Şiran" to "Gümüşhane",
    "Torul" to "Gümüşhane",

    // Hakkari
    "Çukurca" to "Hakkari", "Derecik" to "Hakkari", "Şemdinli" to "Hakkari", "Yüksekova" to "Hakkari",

    // Hatay
    "Altınözü" to "Hatay", "Antakya" to "Hatay", "Arsuz" to "Hatay", "Belen" to "Hatay",
    "Defne" to "Hatay", "Dörtyol" to "Hatay", "Erzin" to "Hatay", "Hassa" to "Hatay",
    "İskenderun" to "Hatay", "Kırıkhan" to "Hatay", "Kumlu" to "Hatay", "Payas" to "Hatay",
    "Reyhanlı" to "Hatay", "Samandağ" to "Hatay", "Yayladağı" to "Hatay",

    // Isparta
    "Aksu" to "Isparta", "Atabey" to "Isparta", "Eğirdir" to "Isparta", "Gelendost" to "Isparta",
    "Gönen" to "Isparta", "Keçiborlu" to "Isparta", "Senirkent" to "Isparta", "Sütçüler" to "Isparta",
    "Şarkikaraağaç" to "Isparta", "Uluborlu" to "Isparta", "Yalvaç" to "Isparta", "Yenişarbademli" to "Isparta",

    // Mersin
    "Akdeniz" to "Mersin", "Anamur" to "Mersin", "Aydıncık" to "Mersin", "Bozyazı" to "Mersin",
    "Çamlıyayla" to "Mersin", "Erdemli" to "Mersin", "Gülnar" to "Mersin", "Mezitli" to "Mersin",
    "Mut" to "Mersin", "Silifke" to "Mersin", "Tarsus" to "Mersin", "Toroslar" to "Mersin",
    "Yenişehir" to "Mersin",

    // İstanbul
    "Adalar" to "İstanbul", "Arnavutköy" to "İstanbul", "Ataşehir" to "İstanbul", "Avcılar" to "İstanbul",
    "Bağcılar" to "İstanbul", "Bahçelievler" to "İstanbul", "Bakırköy" to "İstanbul", "Başakşehir" to "İstanbul",
    "Bayrampaşa" to "İstanbul", "Beşiktaş" to "İstanbul", "Beykoz" to "İstanbul", "Beylikdüzü" to "İstanbul",
    "Beyoğlu" to "İstanbul", "Büyükçekmece" to "İstanbul", "Çatalca" to "İstanbul", "Çekmeköy" to "İstanbul",
    "Esenler" to "İstanbul", "Esenyurt" to "İstanbul", "Eyüpsultan" to "İstanbul", "Fatih" to "İstanbul",
    "Gaziosmanpaşa" to "İstanbul", "Güngören" to "İstanbul", "Kadıköy" to "İstanbul", "Kağıthane" to "İstanbul",
    "Kartal" to "İstanbul", "Küçükçekmece" to "İstanbul", "Maltepe" to "İstanbul", "Pendik" to "İstanbul",
    "Sancaktepe" to "İstanbul", "Sarıyer" to "İstanbul", "Silivri" to "İstanbul", "Sultanbeyli" to "İstanbul",
    "Sultangazi" to "İstanbul", "Şile" to "İstanbul", "Şişli" to "İstanbul", "Tuzla" to "İstanbul",
    "Ümraniye" to "İstanbul", "Üsküdar" to "İstanbul", "Zeytinburnu" to "İstanbul",

    // İzmir
    "Aliağa" to "İzmir", "Balçova" to "İzmir", "Bayındır" to "İzmir", "Bayraklı" to "İzmir",
    "Bergama" to "İzmir", "Beydağ" to "İzmir", "Bornova" to "İzmir", "Buca" to "İzmir",
    "Çeşme" to "İzmir", "Çiğli" to "İzmir", "Dikili" to "İzmir", "Foça" to "İzmir",
    "Gaziemir" to "İzmir", "Güzelbahçe" to "İzmir", "Karabağlar" to "İzmir", "Karaburun" to "İzmir",
    "Karşıyaka" to "İzmir", "Kemalpaşa" to "İzmir", "Kınık" to "İzmir", "Kiraz" to "İzmir",
    "Konak" to "İzmir", "Menderes" to "İzmir", "Menemen" to "İzmir", "Narlıdere" to "İzmir",
    "Ödemiş" to "İzmir", "Seferihisar" to "İzmir", "Selçuk" to "İzmir", "Tire" to "İzmir",
    "Torbalı" to "İzmir", "Urla" to "İzmir",

    // Kars
    "Akyaka" to "Kars", "Arpaçay" to "Kars", "Digor" to "Kars", "Kağızman" to "Kars",
    "Sarıkamış" to "Kars", "Selim" to "Kars", "Susuz" to "Kars",

    // Kastamonu
    "Abana" to "Kastamonu", "Ağlı" to "Kastamonu", "Araç" to "Kastamonu", "Bozkurt" to "Kastamonu",
    "Cide" to "Kastamonu", "Çatalzeytin" to "Kastamonu", "Daday" to "Kastamonu", "Devrekani" to "Kastamonu",
    "Doğanyurt" to "Kastamonu", "Hanönü" to "Kastamonu", "İhsangazi" to "Kastamonu", "İnebolu" to "Kastamonu",
    "Küre" to "Kastamonu", "Pınarbaşı" to "Kastamonu", "Seydiler" to "Kastamonu", "Şenpazar" to "Kastamonu",
    "Taşköprü" to "Kastamonu", "Tosya" to "Kastamonu",

    // Kayseri
    "Akkışla" to "Kayseri", "Bünyan" to "Kayseri", "Develi" to "Kayseri", "Felahiye" to "Kayseri",
    "Hacılar" to "Kayseri", "İncesu" to "Kayseri", "Kocasinan" to "Kayseri", "Melikgazi" to "Kayseri",
    "Özvatan" to "Kayseri", "Pınarbaşı" to "Kayseri", "Sarıoğlan" to "Kayseri", "Sarız" to "Kayseri",
    "Talas" to "Kayseri", "Tomarza" to "Kayseri", "Yahyalı" to "Kayseri", "Yeşilhisar" to "Kayseri",

    // Kocaeli
    "Başiskele" to "Kocaeli", "Çayırova" to "Kocaeli", "Darıca" to "Kocaeli", "Derince" to "Kocaeli",
    "Dilovası" to "Kocaeli", "Gebze" to "Kocaeli", "Gölcük" to "Kocaeli", "İzmit" to "Kocaeli",
    "Kandıra" to "Kocaeli", "Karamürsel" to "Kocaeli", "Kartepe" to "Kocaeli", "Körfez" to "Kocaeli",

    // Konya
    "Ahırlı" to "Konya", "Akören" to "Konya", "Akşehir" to "Konya", "Altınekin" to "Konya",
    "Beyşehir" to "Konya", "Bozkır" to "Konya", "Cihanbeyli" to "Konya", "Çeltik" to "Konya",
    "Çumra" to "Konya", "Derbent" to "Konya", "Derebucak" to "Konya", "Doğanhisar" to "Konya",
    "Emirgazi" to "Konya", "Ereğli" to "Konya", "Güneysınır" to "Konya", "Hadim" to "Konya",
    "Halkapınar" to "Konya", "Hüyük" to "Konya", "Ilgın" to "Konya", "Kadınhanı" to "Konya",
    "Karapınar" to "Konya", "Karatay" to "Konya", "Kulu" to "Konya", "Meram" to "Konya",
    "Sarayönü" to "Konya", "Selçuklu" to "Konya", "Seydişehir" to "Konya", "Taşkent" to "Konya",
    "Tuzlukçu" to "Konya", "Yalıhüyük" to "Konya", "Yunak" to "Konya",

    // Malatya
    "Akçadağ" to "Malatya", "Arapgir" to "Malatya", "Arguvan" to "Malatya", "Battalgazi" to "Malatya",
    "Darende" to "Malatya", "Doğanşehir" to "Malatya", "Doğanyol" to "Malatya", "Hekimhan" to "Malatya",
    "Kale" to "Malatya", "Kuluncak" to "Malatya", "Pütürge" to "Malatya", "Yazıhan" to "Malatya",
    "Yeşilyurt" to "Malatya",

    // Manisa
    "Ahmetli" to "Manisa", "Akhisar" to "Manisa", "Alaşehir" to "Manisa", "Demirci" to "Manisa",
    "Gölmarmara" to "Manisa", "Gördes" to "Manisa", "Kırkağaç" to "Manisa", "Köprübaşı" to "Manisa",
    "Kula" to "Manisa", "Salihli" to "Manisa", "Sarıgöl" to "Manisa", "Saruhanlı" to "Manisa",
    "Selendi" to "Manisa", "Soma" to "Manisa", "Şehzadeler" to "Manisa", "Turgutlu" to "Manisa",
    "Yunusemre" to "Manisa",

    // Kahramanmaraş
    "Afşin" to "Kahramanmaraş", "Andırın" to "Kahramanmaraş", "Çağlayancerit" to "Kahramanmaraş",
    "Dulkadiroğlu" to "Kahramanmaraş", "Ekinözü" to "Kahramanmaraş", "Elbistan" to "Kahramanmaraş",
    "Göksun" to "Kahramanmaraş", "Nurhak" to "Kahramanmaraş", "Onikişubat" to "Kahramanmaraş",
    "Pazarcık" to "Kahramanmaraş", "Türkoğlu" to "Kahramanmaraş",

    // Mardin
    "Artuklu" to "Mardin", "Dargeçit" to "Mardin", "Derik" to "Mardin", "Kızıltepe" to "Mardin",
    "Mazıdağı" to "Mardin", "Midyat" to "Mardin", "Nusaybin" to "Mardin", "Ömerli" to "Mardin",
    "Savur" to "Mardin", "Yeşilli" to "Mardin",

    // Muğla
    "Bodrum" to "Muğla", "Dalaman" to "Muğla", "Datça" to "Muğla", "Fethiye" to "Muğla",
    "Kavaklıdere" to "Muğla", "Köyceğiz" to "Muğla", "Marmaris" to "Muğla", "Menteşe" to "Muğla",
    "Milas" to "Muğla", "Ortaca" to "Muğla", "Seydikemer" to "Muğla", "Ula" to "Muğla",
    "Yatağan" to "Muğla",

    // Muş
    "Bulanık" to "Muş", "Hasköy" to "Muş", "Korkut" to "Muş", "Malazgirt" to "Muş", "Varto" to "Muş",

    // Rize
    "Ardeşen" to "Rize", "Çamlıhemşin" to "Rize", "Çayeli" to "Rize", "Derepazarı" to "Rize",
    "Fındıklı" to "Rize", "Güneysu" to "Rize", "Hemşin" to "Rize", "İkizdere" to "Rize",
    "İyidere" to "Rize", "Kalkandere" to "Rize", "Pazar" to "Rize",

    // Sakarya
    "Adapazarı" to "Sakarya", "Akyazı" to "Sakarya", "Arifiye" to "Sakarya", "Erenler" to "Sakarya",
    "Ferizli" to "Sakarya", "Geyve" to "Sakarya", "Hendek" to "Sakarya", "Karapürçek" to "Sakarya",
    "Karasu" to "Sakarya", "Kaynarca" to "Sakarya", "Kocaali" to "Sakarya", "Pamukova" to "Sakarya",
    "Sapanca" to "Sakarya", "Serdivan" to "Sakarya", "Söğütlü" to "Sakarya", "Taraklı" to "Sakarya",

    // Samsun
    "Alaçam" to "Samsun", "Asarcık" to "Samsun", "Atakum" to "Samsun", "Ayvacık" to "Samsun",
    "Bafra" to "Samsun", "Canik" to "Samsun", "Çarşamba" to "Samsun", "Havza" to "Samsun",
    "İlkadım" to "Samsun", "Kavak" to "Samsun", "Ladik" to "Samsun", "Ondokuzmayıs" to "Samsun",
    "Salıpazarı" to "Samsun", "Tekkeköy" to "Samsun", "Terme" to "Samsun", "Vezirköprü" to "Samsun",
    "Yakakent" to "Samsun",

    // Siirt
    "Baykan" to "Siirt", "Eruh" to "Siirt", "Kurtalan" to "Siirt", "Pervari" to "Siirt",
    "Şirvan" to "Siirt", "Tillo" to "Siirt",

    // Sivas
    "Akıncılar" to "Sivas", "Altınyayla" to "Sivas", "Divriği" to "Sivas", "Doğanşar" to "Sivas",
    "Gemerek" to "Sivas", "Gölova" to "Sivas", "Gürün" to "Sivas", "Hafik" to "Sivas",
    "İmranlı" to "Sivas", "Kangal" to "Sivas", "Koyulhisar" to "Sivas", "Suşehri" to "Sivas",
    "Şarkışla" to "Sivas", "Ulaş" to "Sivas", "Yıldızeli" to "Sivas", "Zara" to "Sivas",

    // Tekirdağ
    "Çerkezköy" to "Tekirdağ", "Çorlu" to "Tekirdağ", "Ergene" to "Tekirdağ", "Hayrabolu" to "Tekirdağ",
    "Kapaklı" to "Tekirdağ", "Malkara" to "Tekirdağ", "Marmaraereğlisi" to "Tekirdağ", "Muratlı" to "Tekirdağ",
    "Saray" to "Tekirdağ", "Süleymanpaşa" to "Tekirdağ", "Şarköy" to "Tekirdağ",

    // Trabzon
    "Akçaabat" to "Trabzon", "Araklı" to "Trabzon", "Arsin" to "Trabzon", "Beşikdüzü" to "Trabzon",
    "Çarşıbaşı" to "Trabzon", "Çaykara" to "Trabzon", "Dernekpazarı" to "Trabzon", "Düzköy" to "Trabzon",
    "Hayrat" to "Trabzon", "Köprübaşı" to "Trabzon", "Maçka" to "Trabzon", "Of" to "Trabzon",
    "Ortahisar" to "Trabzon", "Sürmene" to "Trabzon", "Şalpazarı" to "Trabzon", "Tonya" to "Trabzon",
    "Vakfıkebir" to "Trabzon", "Yomra" to "Trabzon",

    // Şanlıurfa
    "Akçakale" to "Şanlıurfa", "Birecik" to "Şanlıurfa", "Bozova" to "Şanlıurfa", "Ceylanpınar" to "Şanlıurfa",
    "Eyyübiye" to "Şanlıurfa", "Halfeti" to "Şanlıurfa", "Haliliye" to "Şanlıurfa", "Harran" to "Şanlıurfa",
    "Hilvan" to "Şanlıurfa", "Karaköprü" to "Şanlıurfa", "Siverek" to "Şanlıurfa", "Suruç" to "Şanlıurfa",
    "Viranşehir" to "Şanlıurfa",

    // Van
    "Bahçesaray" to "Van", "Başkale" to "Van", "Çaldıran" to "Van", "Çatak" to "Van",
    "Edremit" to "Van", "Erciş" to "Van", "Gevaş" to "Van", "Gürpınar" to "Van",
    "İpekyolu" to "Van", "Muradiye" to "Van", "Özalp" to "Van", "Saray" to "Van",
    "Tuşba" to "Van",

    // Zonguldak
    "Alaplı" to "Zonguldak", "Çaycuma" to "Zonguldak", "Devrek" to "Zonguldak", "Gökçebey" to "Zonguldak",
    "Karadeniz Ereğli" to "Zonguldak", "Kilimli" to "Zonguldak", "Kozlu" to "Zonguldak"
)

/**
 * Bir yerin (İl veya İlçe) plaka kodunu bulur.
 * İl ise doğrudan kendi plakası, İlçe ise bağlı olduğu ilin plakası döner.
 */
fun getPlateCodeForLocation(name: String): Int? {
    // 1. Doğrudan il listesinde mi?
    plateCodes[name]?.let { return it }

    // 2. İlçe haritasında var mı?
    val province = districtToProvince[name]
    if (province != null) {
        return plateCodes[province]
    }

    // 3. Büyük/küçük harf duyarsız kontrol
    val match = districtToProvince.entries.firstOrNull { it.key.equals(name, ignoreCase = true) }
    if (match != null) {
        return plateCodes[match.value]
    }

    return null
}

/**
 * Verilen yer ilçe ise bağlı olduğu ilin adını döndürür.
 */
fun getProvinceOfDistrict(name: String): String? {
    return districtToProvince[name] ?: districtToProvince.entries.firstOrNull {
        it.key.equals(name, ignoreCase = true)
    }?.value
}
