% Carrega imagem e coloca no padrao necessario para servir de entrada
% para uma rede neural, na forma de vetores.

% data: 11/04/2007

clear; clc;

nome_arquivo = '/media/KINGSTON_U3/Documents/images/mandrill.tiff';

A=imread(nome_arquivo,'TIFF');               % Le o arquivo da imagem
B=rgb2gray(A);                              % Converte para niveis de cinza
C=imresize(B,[256 256]);                    % Redimensiona imagem
D=im2col(C,[4 4],'distinct');               % Extrai blocos 4x4 e os rearruma em vetores-coluna de tamanho 16
E=col2im(D,[4 4],[256 256],'distinct');     % Rearranja vetores em blocos para regerar a matriz

D=D';                                       % Tranposicao da matriz de dados

save mandrill.dat D -ascii                  % Dados para treinamento

% Outros comandos uteis
% imnoise
% im2double
% col2im
% J = entropy(I)   % Avalia textura de uma imagem
