#ifndef UTIL_H
#define UTIL_H

#ifdef LINUX_ARRAYLET

#include <sys/mman.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <unistd.h>

#endif

#include <sys/types.h>
#include <stdlib.h>
#include <fcntl.h>

#include <chrono>
#include <iostream>
#include <cstring>

#define ARRAYLET_COUNT 8
#define ARRAYLET_SIZE_CONST 4 // (pagesize)4096(POSIX) | 65536(Windows) * ARRAYLET_SIZE_CONST: 64 KB
#define SIXTEEN 16
#define TWO_HUNDRED_56_MB 268435456
#define ONE_GB 1073741824 // 1GB
#define TWO_GB 2147483648 // 2GB
#define FOUR_GB 4294967296 // 4GB
#define EIGHT_GB 8589934592 // 8GB
#define SIXTEEN_GB 17179869184 // 16GB
#define SIXTY_FOUR_GB 68719476736 // 64GB
#define PADDING_BYTES 128

#define MMAP_FLAG_SHARED_ANON  1
#define MMAP_FLAG_PRIVATE_ANON  2
#define MMAP_FLAG_ANON_TLB  4
#define MMAP_FLAG_SHARED 8
#define MMAP_FLAG_SHARED_FIXED  16
#define MMAP_FLAG_PRIVATE_FIXED  32


class ElapsedTimer {
private:
    char padding0[PADDING_BYTES];
    bool calledStart;
    char padding1[PADDING_BYTES];
    std::chrono::time_point<std::chrono::high_resolution_clock> start;
    char padding2[PADDING_BYTES];
public:
    ElapsedTimer() {
        calledStart = false;
    }
    void startTimer() {
        calledStart = true;
        start = std::chrono::high_resolution_clock::now();
    }
    int64_t getElapsedMicros() {
        if (!calledStart) {
            std::cout << "ERROR: called getElapsedMicros without calling startTimer\n";
            exit(1);
        }
        auto now = std::chrono::high_resolution_clock::now();
        return std::chrono::duration_cast<std::chrono::microseconds>(now - start).count();
    }
};

class PaddedRandom {
private:
    volatile char padding[PADDING_BYTES-sizeof(unsigned int)];
    unsigned int seed;
public:
    PaddedRandom(void) {
        this->seed = 0;
    }
    PaddedRandom(int seed) {
        this->seed = seed;
    }

    void setSeed(int seed) {
        this->seed = seed;
    }

    /** returns pseudorandom x satisfying 0 <= x < n. **/
    unsigned int nextNatural() {
        seed ^= seed << 6;
        seed ^= seed >> 21;
        seed ^= seed << 7;
        return seed;
    }
};

long getPageAlignedOffset(size_t pagesize, long num)
   {
   int remain = num % pagesize;
   if(remain < pagesize / 2)
      return num - remain;
   else
      return num + (pagesize - remain);
   }

#endif /* UTIL_H */

