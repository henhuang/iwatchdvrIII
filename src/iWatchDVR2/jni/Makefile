NDKHOME     =D:/work/Android/tools/ndk/android-ndk-r8c
PEERLIB     =../../../release/android
PEERSRC     =../../../src
FFMPEGLIB   =../../../lib/ffmpeg-0.11.1/release/android
TUTKLIB     =../../../lib/tutk/android

PLATFORM    =9
CC          = ${NDKHOME}/toolchains/arm-linux-androideabi-4.6/prebuilt/windows/bin/arm-linux-androideabi-g++
CFLAGS      = -O2 -Wall -fPIC -fpermissive
CFLAGS     += -DICARCH_ANDROID
CFLAGS     += -I${NDKHOME}/platforms/android-${PLATFORM}/arch-arm/usr/include
CFLAGS     += -I${NDKHOME}/sources/cxx-stl/gnu-libstdc++/4.6/include
CFLAGS     += -I${NDKHOME}/sources/cxx-stl/gnu-libstdc++/4.6/libs/armeabi/include
CFLAGS     += -I${PEERSRC}/LibPeerMedia
CFLAGS     += -I${PEERSRC}/PeerSDK/include

LDFLAGS     = -shared -Wl,-s,-soname,libpeersdk-jni.so --sysroot=${NDKHOME}/platforms/android-${PLATFORM}/arch-arm

LIBS       += -llog -landroid -lEGL -lGLESv2

LIBS       += $(PEERLIB)/libweb.a
LIBS       += ${PEERLIB}/libpeermedia.a
LIBS       += ${PEERLIB}/libpeersdk.a
LIBS       += ${PEERLIB}/libx10.a
LIBS       += ${PEERLIB}/libx11.a
LIBS       += ${PEERLIB}/libx11z.a
LIBS       += ${PEERLIB}/libmoai.a
LIBS       += ${PEERLIB}/libmaya.a
LIBS       += ${PEERLIB}/libpeer.a
LIBS       += ${PEERLIB}/libcore.a
LIBS       += ${FFMPEGLIB}/libavcodec.a
LIBS       += ${FFMPEGLIB}/libavutil.a
LIBS       += ${FFMPEGLIB}/libswscale.a
LIBS       += $(TUTKLIB)/libP2PTunnelAPIs.so

LIBS       += ${NDKHOME}/sources/cxx-stl/gnu-libstdc++/4.6/libs/armeabi/libgnustl_static.a
LIBS       += ${NDKHOME}/sources/cxx-stl/gnu-libstdc++/4.6/libs/armeabi/libsupc++.a
LIBS       += ${NDKHOME}/toolchains/arm-linux-androideabi-4.6/prebuilt/windows/lib/gcc/arm-linux-androideabi/4.6/libgcc.a


OUTPUT      = libpeersdk-jni
OUTPUTEXT   = so
OUTPUTDIR   = _ANDROID-Bin

STRIP = arm-linux-androideabi-strip 

PROGS = libpeersdk-jni

all: $(PROGS) install

%: %.o
	$(CC) $(LDFLAGS) -o $@.$(OUTPUTEXT) $^ $(LIBS)
%.o: %.cpp
	$(CC) $(CFLAGS) -c -o $@ $<

##########
$(PROGS) +: JNIonLoad.o

$(PROGS) +: _MediaDispatcher.o
$(PROGS) +: _TimeSpan.o
$(PROGS) +: _TimeRange.o
$(PROGS) +: _PeerAudio.o
$(PROGS) +: _PeerChannel.o
$(PROGS) +: _PeerHDD.o
$(PROGS) +: _PeerLog.o
$(PROGS) +: _PeerPTZ.o
$(PROGS) +: _PeerRecorder.o
$(PROGS) +: _PeerRecordList.o
$(PROGS) +: _PeerRelay.o
$(PROGS) +: _PeerStream.o
$(PROGS) +: _PeerVideo.o
$(PROGS) +: _Peer.o

$(PROGS) +: libpeersdk-jni.o

install:
	rm -f ../libs/armeabi/$(OUTPUT).$(OUTPUTEXT)
	cp $(OUTPUT).$(OUTPUTEXT) ../libs/armeabi

clean:
	rm -f *.o
