백엔드관련









명령어	                                    설명
docker-compose up	                        docker-compose.yml 기반으로 컨테이너 실행
docker-compose up -d	                    백그라운드에서 컨테이너 실행 (detached 모드)
docker-compose down	                        모든 컨테이너 종료 및 삭제
docker-compose stop	                        모든 컨테이너 중지
docker-compose start	                    모든 컨테이너 재시작
docker-compose restart	                    모든 컨테이너 재시작
docker-compose ps	                        현재 실행 중인 서비스 목록 조회
docker-compose logs <서비스명>	            특정 서비스의 로그 확인
docker-compose logs -f	                    모든 서비스의 로그 실시간 확인
docker-compose exec <서비스명> /bin/sh	    특정 서비스의 컨테이너 내부 접속
docker-compose build	                    docker-compose.yml 기반으로 이미지 빌드
docker-compose rm -f	                    모든 컨테이너 삭제 (이미지 제외)

docker-compose -f infra/docker-compose.local.yml build                                                  # 빌드만
docker-compose -f infra/docker-compose.local.yml build auth-service                                     # 특정 파일만 빌드

docker-compose -f infra/docker-compose.local.yml up -d --build                                          # 빌드 후 실행 
docker-compose -f infra/docker-compose.local.yml up -d --build auth-service                             # 특정 파일만 빌드 후 실행

docker-compose -f infra/docker-compose.local.yml start                                                  # 스타트
docker-compose -f infra/docker-compose.local.yml stop                                                   # 중지
docker-compose -f infra/docker-compose.local.yml stop auth-service                                      # 특정 서비스 중지

docker-compose -f infra/docker-compose.local.yml restart                                                # 재시작
docker-compose -f infra/docker-compose.local.yml down                                                   # 다운
docker-compose -f infra/docker-compose.local.yml logs                                                   # 로그
docker-compose -f infra/docker-compose.local.yml logs -f --no-log-prefix                                # 로그 실시간
docker-compose -f infra/docker-compose.local.yml logs -f gateway-service auth-service --no-log-prefix   # 특정 서비스 로그 실시간



명령어	                                설명
docker network ls	                    현재 존재하는 네트워크 목록 조회
docker network create <네트워크명>	    새로운 네트워크 생성
docker network inspect <네트워크명>	    특정 네트워크 정보 확인
docker network rm <네트워크명>	        특정 네트워크 삭제
docker network prune	                사용되지 않는 네트워크 삭제


5️⃣ 컨테이너 정리 명령어 (필수)
컨테이너와 이미지가 쌓이면 시스템이 느려질 수 있음. 정리하는 명령어.

명령어	설명
docker system prune	사용하지 않는 컨테이너, 네트워크, 이미지 삭제
docker volume prune	사용하지 않는 볼륨 삭제
docker image prune -a	사용하지 않는 모든 이미지 삭제
docker container prune	종료된 모든 컨테이너 삭제