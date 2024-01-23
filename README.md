Programación Concurrente - 2022 - Trabajo Practico Final

# Picasso - Sistema de Procesamiento de Imágenes basado en Redes de Petri
Picasso es un sistema modular de procesamiento de imágenes basado en redes de Petri. Este sistema proporciona una plataforma flexible y escalable para la ejecución concurrente de tareas de procesamiento de imágenes. Utiliza una implementación de red de Petri para modelar y coordinar las interacciones entre diferentes componentes del sistema, permitiendo una gestión eficiente y ordenada de las transiciones.

> [!IMPORTANT]
> Este proyecto implementa una simulación de un sistema de procesamiento de imágenes basado en redes de Petri. No es un sistema de procesamiento de imágenes real.

## Autores

- **Robledo, Valentín**
- **Bottini, Franco Nicolas**
- **Lencina, Aquiles Benjamin**

## ¿Cómo clonar este repositorio?

```console
git clone https://github.com/FrancoNB/TP-Final-Concurrente-2022.git
```

## ¿Cómo utilizar?

Vamos al directorio principal del proyecto y utilizamos el siguiente comando

```console
./gradlew run
```

> [!NOTE]
> Se requier Java JDK 8 o superior.

## Estructura del Proyecto
El proyecto está organizado en paquetes que encapsulan diferentes aspectos del sistema:

### Paquete `com.picasso.Policy`
Este paquete contiene clases relacionadas con la implementación de políticas de ejecución de transiciones.

- `Policy`: Interfaz que define el contrato para implementar políticas de transición.
  
- `PolicyMinTransitions`: Implementa una política que elige la transición con menos transiciones ejecutadas y menos invariantes ejecutados.

- `PolicyRandom`: Implementa una política que elige una transición de manera aleatoria.

### Paquete `com.picasso.Segment`
Este paquete contiene la clase que representa un segmento de la red de Petri.

- `Segment`: Clase que modela un conjunto de transiciones que se ejecutan juntas. Proporciona una forma de organizar y coordinar las ejecuciones simultáneas.

### Paquete `com.picasso.Artist`
Contiene clases que representan "artistas", componentes que realizan acciones específicas durante la ejecución de las transiciones.

- `Artist`: Interfaz que define el contrato para implementar artistas asociados a transiciones.

- `Diffuser`: Clase que implementa un artista para la transición de difusión.

- `RGBPainter`: Clase que implementa un artista para la transición de pintura RGB.

- `SuperPositioner`: Clase que implementa un artista para la transición de superposición.

- `BWPainter`: Clase que implementa un artista para la transición de pintura blanco y negro.

- `Compressor`: Clase que implementa un artista para la transición de compresión.

- `PainterPattern`: Clase que implementa un artista para la transición de patrón de pintura.

- `FilterPattern`: Clase que implementa un artista para la transición de patrón de filtro.

- `TemplatePattern`: Clase que implementa un artista para la transición de patrón de plantilla.

> [!NOTE]
> La tarea realizada por cada artista no se implementa, se simula por medio de retardos de distinta duración para cada uno de los artistas.

### Paquete `com.picasso.Monitor`
Contiene la clase que gestiona la ejecución y el monitoreo de la red de Petri, manteniendo registros detallados de las transiciones y los invariantes ejecutados.

`Monitor`: Clase que monitorea la ejecución de la red de Petri y registra estadísticas detalladas.