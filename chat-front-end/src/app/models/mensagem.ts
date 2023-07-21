export interface Mensagem{

    idMensagem: number;
    idGrupo: number;
    username: string;
    mensagem: string;
    data: Date;
    notificacao: boolean;
}