#ifndef GAME_OF_LIFE_V2_0_IO_H
#define GAME_OF_LIFE_V2_0_IO_H


class State;
class QIODevice;

State &&readState(QIODevice *device);

#endif //GAME_OF_LIFE_V2_0_IO_H
